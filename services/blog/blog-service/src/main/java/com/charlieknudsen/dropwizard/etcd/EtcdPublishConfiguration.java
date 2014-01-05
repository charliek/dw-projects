package com.charlieknudsen.dropwizard.etcd;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

class EtcdPublishConfiguration {

    // Ideas for future versions
//    public String serviceType = "http";
//    public String environment;
//    public String version;

    /**
     * Application name to publish to etcd. This will default to the main service name if unset.
     */
    @JsonProperty
    String name;

    @JsonProperty
    @NotBlank
    String hostName;

    /**
     * Port to publish to etcd. This will default to the main service port if unset.
     */
    @JsonProperty
    Integer port;

    @Min(1)
    Long ttlSeconds = 10L;

    @Min(1)
    Long publishSeconds = 3L;

}
