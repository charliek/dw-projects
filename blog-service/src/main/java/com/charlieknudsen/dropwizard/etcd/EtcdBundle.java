package com.charlieknudsen.dropwizard.etcd;

import com.charlieknudsen.etcd.EtcdClient;
import com.charlieknudsen.ribbon.etcd.EtcdPublisher;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.jetty.ConnectorFactory;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.jetty.HttpsConnectorFactory;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.server.ServerFactory;
import io.dropwizard.server.SimpleServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class EtcdBundle<T extends Configuration> implements ConfiguredBundle<T> {

    private static final Logger log = LoggerFactory.getLogger(EtcdBundle.class);

    private EtcdClient client;

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        // Nothing to initialize
    }

    private int unableToDiscoverPort() {
        throw new IllegalArgumentException(
                "Unable to discover application port. Please specify port to publish to etcd");
    }

    private int discoverPort(ConnectorFactory factory) {
        if (factory instanceof HttpsConnectorFactory) {
            return ((HttpsConnectorFactory) factory).getPort();
        } else if (factory instanceof HttpConnectorFactory) {
            return ((HttpConnectorFactory) factory).getPort();
        } else {
            return unableToDiscoverPort();
        }
    }

    private int discoverPort(T configuration) {
        ServerFactory factory = configuration.getServerFactory();
        if (factory instanceof SimpleServerFactory) {
            return discoverPort(((SimpleServerFactory) factory).getConnector());
        } else if (factory instanceof DefaultServerFactory) {
            DefaultServerFactory defaultFactory = (DefaultServerFactory) factory;
            List<ConnectorFactory> connectorFactories = defaultFactory.getApplicationConnectors();
            if (connectorFactories.size() > 1) {
                throw new IllegalArgumentException(
                        "Multiple application ports found. Please specify port to publish to etcd"
                );
            }
            return discoverPort(connectorFactories.get(0));
        }
        return unableToDiscoverPort();
    }

    private void setupBundle(T configuration, Environment environment, EtcdConfiguration etcdConfig) {
        log.info("Initializing etcd client with hosts {}", etcdConfig.hosts);
        client = new EtcdClient(etcdConfig.hosts);

        final EtcdPublishConfiguration publishConfiguration = etcdConfig.publish;
        if (publishConfiguration != null) {
            final String appName = (publishConfiguration.name != null)
                    ? publishConfiguration.name : environment.getName();

            final int port = (publishConfiguration.port != null)
                    ? publishConfiguration.port : discoverPort(configuration);

            final EtcdPublisher publisher = new EtcdPublisher(client, publishConfiguration.hostName, port, appName);

            environment.lifecycle().manage(new Managed() {
                @Override
                public void start() throws Exception {
                    log.info("Initializing etcd publishing for service {}, {}:{}, ttl {}, publish freq {}",
                            appName,
                            publishConfiguration.hostName,
                            port,
                            publishConfiguration.ttlSeconds,
                            publishConfiguration.publishSeconds);
                    publisher.publishOnInterval(publishConfiguration.ttlSeconds, publishConfiguration.publishSeconds);
                }

                @Override
                public void stop() throws Exception {
                    publisher.stop();
                }
            });
        }
    }

    @Override
    public void run(T configuration, Environment environment) {
        EtcdConfiguration etcdConfig = getEtcdConfiguration(configuration);
        if(etcdConfig.enabled) {
            setupBundle(configuration, environment, etcdConfig);
        } else {
            log.info("Etcd disabled. Skipping etcd initialization");
        }
    }

    public abstract EtcdConfiguration getEtcdConfiguration(T configuration);

    public EtcdClient getClient() {
        return client;
    }
}
