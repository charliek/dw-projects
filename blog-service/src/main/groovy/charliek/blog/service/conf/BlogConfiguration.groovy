package charliek.blog.service.conf

import com.charlieknudsen.dw.common.conf.GraphiteConfiguration
import com.charlieknudsen.dropwizard.etcd.EtcdConfiguration
import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration
import io.dropwizard.db.DataSourceFactory

import javax.validation.Valid
import javax.validation.constraints.NotNull

class BlogConfiguration extends Configuration {

    @Valid
    @NotNull
    @JsonProperty
    DataSourceFactory database = new DataSourceFactory()

    @Valid
    @NotNull
    @JsonProperty
    GraphiteConfiguration graphite = new GraphiteConfiguration()

    @Valid
    EtcdConfiguration etcd = new EtcdConfiguration()

}
