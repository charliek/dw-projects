package com.charlieknudsen.dropwizard.etcd;

import com.charlieknudsen.etcd.EtcdClient;
import com.charlieknudsen.ribbon.etcd.EtcdPublisher;
import com.yammer.dropwizard.ConfiguredBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class EtcdBundle<T extends Configuration> implements ConfiguredBundle<T> {

    private static final Logger log = LoggerFactory.getLogger(EtcdBundle.class);

    private EtcdClient client;

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        // Nothing to initialize
    }

    @Override
    public void run(T configuration, Environment environment) {
        EtcdConfiguration etcdConfig = getEtcdConfiguration(configuration);
        log.info("Initializing etcd client with hosts {}", etcdConfig.hosts);
        client = new EtcdClient(etcdConfig.hosts);

        final EtcdPublishConfiguration publishConfiguration = etcdConfig.publish;
        if (publishConfiguration != null) {
            final String appName = (publishConfiguration.name != null)
                    ? publishConfiguration.name : environment.getName();

            final int port = (publishConfiguration.port != null)
                    ? publishConfiguration.port : configuration.getHttpConfiguration().getPort();

            final EtcdPublisher publisher = new EtcdPublisher(client, publishConfiguration.hostName, port, appName);

            environment.manage(new Managed() {
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

    public abstract EtcdConfiguration getEtcdConfiguration(T configuration);

    public EtcdClient getClient() {
        return client;
    }
}
