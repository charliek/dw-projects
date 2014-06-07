package com.charlieknudsen.dropwizard.etcd;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;

public class EtcdConfiguration {

    @JsonProperty
    @NotBlank
    public String hosts;

    @JsonProperty
    @Valid
    public EtcdPublishConfiguration publish;

    @JsonProperty
    @Valid
    public boolean enabled = true;

}
