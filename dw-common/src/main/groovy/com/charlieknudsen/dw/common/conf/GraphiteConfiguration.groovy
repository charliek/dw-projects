package com.charlieknudsen.dw.common.conf

import com.fasterxml.jackson.annotation.JsonProperty

import javax.validation.constraints.NotNull

class GraphiteConfiguration {

    @NotNull
    @JsonProperty
    Boolean enabled

    @JsonProperty
    String host

    @JsonProperty
    int port = 2003

    @JsonProperty
    int reportingIntervalSecs = 30

    @JsonProperty
    String environment
}
