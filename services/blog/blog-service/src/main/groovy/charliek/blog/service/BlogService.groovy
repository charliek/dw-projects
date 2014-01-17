package charliek.blog.service

import charliek.blog.service.conf.BlogConfiguration
import charliek.blog.service.dao.AuthorDAO
import charliek.blog.service.dao.PostDAO
import charliek.blog.service.domain.AuthorEntity
import charliek.blog.service.domain.PostEntity
import charliek.blog.service.resources.AuthorResource
import charliek.blog.service.resources.PostResource
import com.charlieknudsen.dropwizard.etcd.EtcdBundle
import com.charlieknudsen.dropwizard.etcd.EtcdConfiguration
import com.charlieknudsen.dw.common.exceptions.NotFoundExceptionMapper
import com.charlieknudsen.dw.common.exceptions.ValidationExceptionMapper
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.ImmutableList
import io.dropwizard.Application
import io.dropwizard.assets.AssetsBundle
import io.dropwizard.db.DataSourceFactory
import io.dropwizard.hibernate.HibernateBundle
import io.dropwizard.hibernate.SessionFactoryFactory
import io.dropwizard.migrations.MigrationsBundle
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.hibernate.SessionFactory

class BlogService extends Application<BlogConfiguration> {

    final String name = 'blog-service'

    public static final List<Class<?>> SERVICE_ENTITIES = [
            PostEntity,
            AuthorEntity
    ]
    protected final AssetsBundle assetsBundle = new AssetsBundle()

    MigrationsBundle<BlogConfiguration> buildMigrationsBundle() {
        new MigrationsBundle<BlogConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(BlogConfiguration configuration) {
                return configuration.database
            }
        }
    }

    HibernateBundle<BlogConfiguration> buildHibernateBundle() {
        ImmutableList entities = ImmutableList.copyOf(SERVICE_ENTITIES)
        new HibernateBundle<BlogConfiguration>(entities, new SessionFactoryFactory()) {
            @Override
            public DataSourceFactory getDataSourceFactory(BlogConfiguration configuration) {
                return configuration.database
            }
        }
    }

    EtcdBundle etcdBundle = new EtcdBundle<BlogConfiguration>() {
        @Override
        EtcdConfiguration getEtcdConfiguration(BlogConfiguration configuration) {
            return configuration.etcd
        }
    }

    protected MigrationsBundle<BlogConfiguration> migrationsBundle = buildMigrationsBundle()
    protected HibernateBundle<BlogConfiguration> hibernateBundle = buildHibernateBundle()

    public static void main(String[] args) throws Exception {
        new BlogService().run(args)
    }

    @Override
    void initialize(Bootstrap bootstrap) {
        bootstrap.addBundle(assetsBundle)
        bootstrap.addBundle(migrationsBundle)
        bootstrap.addBundle(hibernateBundle)
        bootstrap.addBundle(etcdBundle)
    }

    @Override
    void run(BlogConfiguration configuration, Environment environment) throws Exception {
        environment.objectMapper.disable(MapperFeature.AUTO_DETECT_IS_GETTERS)
        environment.jersey().register(NotFoundExceptionMapper)
        environment.jersey().register(ValidationExceptionMapper)

        SessionFactory sessionFactory = hibernateBundle.sessionFactory
        ObjectMapper objectMapper = environment.objectMapper

        AuthorDAO authorDAO = new AuthorDAO(sessionFactory)
        PostDAO postDAO = new PostDAO(sessionFactory, authorDAO)

        environment.jersey().register(new PostResource(postDAO, objectMapper))
        environment.jersey().register(new AuthorResource(postDAO, authorDAO, objectMapper))
    }
}
