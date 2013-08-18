package charliek.blog.service

import charliek.blog.service.conf.BlogConfiguration
import charliek.blog.service.dao.PostDAO
import charliek.blog.service.domain.AuthorEntity
import charliek.blog.service.domain.PostEntity
import charliek.blog.service.resources.PostResource
import charliek.dw.exceptions.NotFoundExceptionMapper
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.yammer.dropwizard.Service
import com.yammer.dropwizard.assets.AssetsBundle
import com.yammer.dropwizard.config.Bootstrap
import com.yammer.dropwizard.config.Environment
import com.yammer.dropwizard.db.DatabaseConfiguration
import com.yammer.dropwizard.hibernate.HibernateBundle
import com.yammer.dropwizard.migrations.MigrationsBundle
import org.hibernate.SessionFactory

class BlogService extends Service<BlogConfiguration> {

    final String serviceName = 'blog'

    public static final List<Class<?>> SERVICE_ENTITIES = [
            PostEntity,
            AuthorEntity
    ]
    protected final AssetsBundle assetsBundle = new AssetsBundle()

    protected MigrationsBundle<BlogConfiguration> migrationsBundle = new MigrationsBundle<BlogConfiguration>() {
        @Override
        public DatabaseConfiguration getDatabaseConfiguration(BlogConfiguration configuration) {
            return configuration.database
        }
    }

    protected HibernateBundle<BlogConfiguration> hibernateBundle =
        new HibernateBundle<BlogConfiguration>(SERVICE_ENTITIES) {
        @Override
        public DatabaseConfiguration getDatabaseConfiguration(BlogConfiguration configuration) {
            return configuration.database
        }
    }

    public static void main(String[] args) throws Exception {
        new BlogService().run(args)
    }

    @Override
    void initialize(Bootstrap bootstrap) {
        bootstrap.name = serviceName
        bootstrap.addBundle(assetsBundle)
        bootstrap.addBundle(migrationsBundle)
        bootstrap.addBundle(hibernateBundle)
    }

    @Override
    void run(BlogConfiguration configuration, Environment environment) throws Exception {
        environment.objectMapperFactory.disable(MapperFeature.AUTO_DETECT_IS_GETTERS)
        environment.addProvider(NotFoundExceptionMapper)

        SessionFactory sessionFactory = hibernateBundle.sessionFactory
        ObjectMapper objectMapper = environment.objectMapperFactory.build()

        PostDAO postDAO = new PostDAO(sessionFactory)

        environment.addResource(new PostResource(postDAO, objectMapper))
    }
}
