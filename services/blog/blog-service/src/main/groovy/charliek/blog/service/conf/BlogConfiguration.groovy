package charliek.blog.service.conf

import charliek.dw.conf.GraphiteConfiguration
import com.fasterxml.jackson.annotation.JsonProperty
import com.yammer.dropwizard.config.Configuration
import com.yammer.dropwizard.db.DatabaseConfiguration

import javax.validation.Valid
import javax.validation.constraints.NotNull

class BlogConfiguration extends Configuration {

    @Valid
    @NotNull
    @JsonProperty
    DatabaseConfiguration database = new DatabaseConfiguration()

    @Valid
    @NotNull
    @JsonProperty
    GraphiteConfiguration graphite = new GraphiteConfiguration()

}
