package charliek.helloworld.service

import charliek.dw.exceptions.NotFoundExceptionMapper
import charliek.dw.exceptions.ValidationExceptionMapper
import charliek.helloworld.conf.HelloWorldConfiguration
import charliek.helloworld.resources.BasicResource
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.yammer.dropwizard.Service
import com.yammer.dropwizard.assets.AssetsBundle
import com.yammer.dropwizard.config.Bootstrap
import com.yammer.dropwizard.config.Environment

class HelloWorldService extends Service<HelloWorldConfiguration> {

    final String serviceName = 'helloworld'

    protected final AssetsBundle assetsBundle = new AssetsBundle()

    public static void main(String[] args) throws Exception {
        new HelloWorldService().run(args)
    }

    @Override
    void initialize(Bootstrap bootstrap) {
        bootstrap.name = serviceName
        bootstrap.addBundle(assetsBundle)
    }

    @Override
    void run(HelloWorldConfiguration configuration, Environment environment) throws Exception {
        environment.objectMapperFactory.disable(MapperFeature.AUTO_DETECT_IS_GETTERS)
        environment.addProvider(NotFoundExceptionMapper)
        environment.addProvider(ValidationExceptionMapper)
        ObjectMapper objectMapper = environment.objectMapperFactory.build()
        environment.addResource(new BasicResource(objectMapper))
    }
}
