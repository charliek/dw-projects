package charliek.blog.service

import charliek.blog.service.conf.BlogConfiguration
import charliek.blog.service.dao.AuthorDAO
import charliek.blog.service.dao.PostDAO
import charliek.blog.service.domain.AuthorEntity
import charliek.blog.service.domain.PostEntity
import charliek.blog.service.resources.AuthorResource
import charliek.blog.service.resources.PostResource
import charliek.dw.exceptions.NotFoundExceptionMapper
import charliek.dw.exceptions.ValidationExceptionMapper
import com.charlieknudsen.dropwizard.etcd.EtcdBundle
import com.charlieknudsen.dropwizard.etcd.EtcdConfiguration
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.ImmutableList
import com.yammer.dropwizard.Service
import com.yammer.dropwizard.assets.AssetsBundle
import com.yammer.dropwizard.config.Bootstrap
import com.yammer.dropwizard.config.Environment
import com.yammer.dropwizard.db.DatabaseConfiguration
import com.yammer.dropwizard.hibernate.HibernateBundle
import com.yammer.dropwizard.hibernate.SessionFactoryFactory
import com.yammer.dropwizard.migrations.MigrationsBundle
import org.hibernate.SessionFactory

class BlogService extends Service<BlogConfiguration> {

    final String serviceName = 'blog-service'

    public static final List<Class<?>> SERVICE_ENTITIES = [
            PostEntity,
            AuthorEntity
    ]
    protected final AssetsBundle assetsBundle = new AssetsBundle()

    MigrationsBundle<BlogConfiguration> buildMigrationsBundle() {
        new MigrationsBundle<BlogConfiguration>() {
            @Override
            public DatabaseConfiguration getDatabaseConfiguration(BlogConfiguration configuration) {
                return configuration.database
            }
        }
    }

    HibernateBundle<BlogConfiguration> buildHibernateBundle() {
        ImmutableList entities = ImmutableList.copyOf(SERVICE_ENTITIES)
        new HibernateBundle<BlogConfiguration>(entities, new SessionFactoryFactory()) {
            @Override
            public DatabaseConfiguration getDatabaseConfiguration(BlogConfiguration configuration) {
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
        bootstrap.name = serviceName
        bootstrap.addBundle(assetsBundle)
        bootstrap.addBundle(migrationsBundle)
        bootstrap.addBundle(hibernateBundle)
        bootstrap.addBundle(etcdBundle)
    }

    @Override
    void run(BlogConfiguration configuration, Environment environment) throws Exception {
        environment.objectMapperFactory.disable(MapperFeature.AUTO_DETECT_IS_GETTERS)
        environment.addProvider(NotFoundExceptionMapper)
        environment.addProvider(ValidationExceptionMapper)

        SessionFactory sessionFactory = hibernateBundle.sessionFactory
        ObjectMapper objectMapper = environment.objectMapperFactory.build()

        AuthorDAO authorDAO = new AuthorDAO(sessionFactory)
        PostDAO postDAO = new PostDAO(sessionFactory, authorDAO)

        environment.addResource(new PostResource(postDAO, objectMapper))
        environment.addResource(new AuthorResource(postDAO, authorDAO, objectMapper))
    }
}
