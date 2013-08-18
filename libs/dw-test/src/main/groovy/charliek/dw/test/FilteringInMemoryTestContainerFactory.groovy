package charliek.dw.test

import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.config.ClientConfig
import com.sun.jersey.api.client.config.DefaultClientConfig
import com.sun.jersey.api.client.filter.LoggingFilter
import com.sun.jersey.api.core.ResourceConfig
import com.sun.jersey.spi.container.WebApplication
import com.sun.jersey.spi.container.WebApplicationFactory
import com.sun.jersey.test.framework.AppDescriptor
import com.sun.jersey.test.framework.LowLevelAppDescriptor
import com.sun.jersey.test.framework.impl.container.inmemory.TestResourceClientHandler
import com.sun.jersey.test.framework.spi.container.TestContainer
import com.sun.jersey.test.framework.spi.container.TestContainerFactory

import javax.ws.rs.core.UriBuilder

/**
 * Container factory for creating Jersey framework once per test
 * from https://github.com/mlex/jerseytest/blob/master/mjl-jersey-server/src/
 * test/java/de/codecentric/mjl/jerseytest/helpers/FilteringInMemoryTestContainerFactory.java
 */
class FilteringInMemoryTestContainerFactory implements TestContainerFactory {

    @Override
    public Class<LowLevelAppDescriptor> supports() {
        return LowLevelAppDescriptor
    }

    @Override
    public TestContainer create(URI baseUri, AppDescriptor ad) {
        if (!(ad instanceof LowLevelAppDescriptor)) {
            throw new IllegalArgumentException(
                    'The application descriptor must be an instance of LowLevelAppDescriptor')
        }
        return new FilteringInMemoryTestContainer(baseUri, (LowLevelAppDescriptor) ad)
    }

    private static class FilteringInMemoryTestContainer implements TestContainer {

        final URI baseUri
        final ResourceConfig resourceConfig
        final WebApplication webApp

        private FilteringInMemoryTestContainer(URI baseUri, LowLevelAppDescriptor ad) {
            this.baseUri = UriBuilder.fromUri(baseUri).build()

            this.resourceConfig = ad.resourceConfig
            this.webApp = initiateWebApplication()
        }

        @Override
        public Client getClient() {
            ClientConfig clientConfig = null
            Set<Object> providerSingletons = resourceConfig.providerSingletons

            if (!providerSingletons.isEmpty()) {
                clientConfig = new DefaultClientConfig()
                providerSingletons.each {
                    clientConfig.singletons.add(it)
                }
            }

            Client client = (clientConfig == null) ? new Client(new TestResourceClientHandler(
                    baseUri, webApp)) : new Client(new TestResourceClientHandler(baseUri, webApp),
                    clientConfig)

            client.addFilter(new LoggingFilter())
            return client

        }

        @Override
        public URI getBaseUri() {
            return baseUri
        }

        @Override
        public void start() {
            if (!webApp.isInitiated()) {
                webApp.initiate(resourceConfig)
            }
        }

        @Override
        public void stop() {
            if (webApp.isInitiated()) {
                webApp.destroy()
            }
        }

        private WebApplication initiateWebApplication() {
            WebApplication webapp = WebApplicationFactory.createWebApplication()
            return webapp
        }
    }
}