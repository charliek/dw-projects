package com.charlieknudsen.dropwizard.etcd;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;

public class EtcdConfiguration extends Configuration {

    @JsonProperty
    @NotBlank
    public String hosts;

    @JsonProperty
    @Valid
    public EtcdPublishConfiguration publish;

}
