package com.charlieknudsen.dw.test

import com.charlieknudsen.dw.common.ObjectMapperFactory
import com.charlieknudsen.dw.common.exceptions.NotFoundExceptionMapper
import com.charlieknudsen.dw.common.exceptions.ValidationExceptionMapper
import com.codahale.metrics.MetricRegistry
import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.GenericType
import com.sun.jersey.test.framework.AppDescriptor
import com.sun.jersey.test.framework.LowLevelAppDescriptor
import com.sun.jersey.test.framework.spi.container.TestContainer
import com.sun.jersey.test.framework.spi.container.TestContainerException
import com.sun.jersey.test.framework.spi.container.TestContainerFactory
import io.dropwizard.jersey.DropwizardResourceConfig
import io.dropwizard.jersey.jackson.JacksonMessageBodyProvider
import spock.lang.Shared
import spock.lang.Specification

import javax.validation.Validation
import javax.validation.Validator
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.UriBuilder

/**
 * Base class for unit testing resources with in-memory Jersey
 *
 * Graciously taken from https://github.com/mlex/jerseytest/blob/master/mjl-jersey-server/src/
 * test/java/de/codecentric/mjl/jerseytest/helpers/FastJerseyTest.java
 */
abstract class ResourceSpecification extends Specification {

    @Shared DropwizardResourceConfig resourceConfig
    @Shared TestContainerFactory testContainerFactory
    @Shared TestContainer testContainer
    @Shared Client client
    @Shared Validator validator

    def setupSpec() {
        validator = Validation.buildDefaultValidatorFactory().validator
        MetricRegistry registry = new MetricRegistry()
        resourceConfig = DropwizardResourceConfig.forTesting(registry)
        addProvider(new JacksonMessageBodyProvider(new ObjectMapperFactory().build(), validator))

        // TODO need to make this abstract so it can vary based on the service
        addProvider(NotFoundExceptionMapper)
        addProvider(ValidationExceptionMapper)
    }

    void cleanupSpec() {
        testContainer?.stop()
        testContainer = null
        client = null
    }

    def setup() {
        if (testContainer == null) {
            setupResources()

            initServer()
            startServer()
        }
    }

    abstract void setupResources()

    protected void initServer() {
        AppDescriptor appDescriptor = new LowLevelAppDescriptor.Builder(resourceConfig).build()
        TestContainerFactory factory = testContainerFactory ?: new FilteringInMemoryTestContainerFactory()
        URI uri = UriBuilder.fromUri('http://localhost/').port(getPort(9998)).build()
        testContainer = factory.create(uri, appDescriptor)
        client = testContainer.client ?: Client.create(appDescriptor.clientConfig)
    }

    protected void startServer() {
        if (testContainer != null) {
            testContainer.start()
        }
    }

    protected void addProvider(Class provider) {
        resourceConfig.classes.add(provider)
    }

    protected void addProvider(Object provider) {
        resourceConfig.singletons.add(provider)
    }

    protected void addResource(Object singleton) {
        resourceConfig.singletons.add(singleton)
    }

    protected void addResource(Class clazz) {
        resourceConfig.classes.add(clazz)
    }

    protected void addFeature(String feature, Boolean value) {
        resourceConfig.features.put(feature, value)
    }

    protected void addProperty(String property, Object value) {
        resourceConfig.properties.put(property, value)
    }

    protected void addRequestFilter(Object filter) {
        resourceConfig.containerRequestFilters.add(filter)
    }

    protected void addResponseFilter(Object filter) {
        resourceConfig.containerResponseFilters.add(filter)
    }

    protected <T> T request(String endPoint, GenericType<T> type) {
        client.resource(endPoint).get(type)
    }

    protected <T> T request(Object endPoint, GenericType<T> type) {
        client.resource(endPoint.toString()).get(type)
    }

    protected <T> T postJson(String endPoint, GenericType<T> type, Object payload) {
        client.resource(endPoint).entity(payload, MediaType.APPLICATION_JSON_TYPE).post(type)
    }

    protected <T> T postJson(Object endPoint, GenericType<T> type, Object payload) {
        client.resource(endPoint.toString()).entity(payload, MediaType.APPLICATION_JSON_TYPE).post(type)
    }

    /**
     * Returns the port to be used in the base URI.
     * @param defaultPort default port
     * @return The HTTP port of the URI
     */
    protected int getPort(int defaultPort) {
        String port = System.getProperty('jersey.test.port')
        if (port) {
            try {
                return Integer.parseInt(port)
            } catch (NumberFormatException e) {
                throw new TestContainerException('jersey.test.port with a ' +
                        'value of "' + port + '" is not a valid integer.', e)
            }
        }

        port = System.getProperty('JERSEY_HTTP_PORT')
        if (port) {
            try {
                return Integer.parseInt(port)
            } catch (NumberFormatException e) {
                throw new TestContainerException('JERSEY_HTTP_PORT with a ' +
                        'value of "' + port + '" is not a valid integer.', e)
            }
        }
        return defaultPort
    }

}
