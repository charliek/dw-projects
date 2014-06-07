package charliek.helloworld.service

import charliek.helloworld.conf.HelloWorldConfiguration
import charliek.helloworld.resources.BasicResource
import com.charlieknudsen.dw.common.exceptions.NotFoundExceptionMapper
import com.charlieknudsen.dw.common.exceptions.ValidationExceptionMapper
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import io.dropwizard.Application
import io.dropwizard.assets.AssetsBundle
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment

class HelloWorldService extends Application<HelloWorldConfiguration> {

    protected final AssetsBundle assetsBundle = new AssetsBundle()

    String name = 'helloworld'

    public static void main(String[] args) throws Exception {
        new HelloWorldService().run(args)
    }

    @Override
    void initialize(Bootstrap bootstrap) {
        bootstrap.addBundle(assetsBundle)
    }

    @Override
    void run(HelloWorldConfiguration configuration, Environment environment) throws Exception {
        environment.objectMapper
                .disable(MapperFeature.AUTO_DETECT_IS_GETTERS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(JsonParser.Feature.ALLOW_COMMENTS, true)
        environment.jersey().register(NotFoundExceptionMapper)
        environment.jersey().register(ValidationExceptionMapper)
        environment.jersey().register(new BasicResource(environment.objectMapper))
    }
}
