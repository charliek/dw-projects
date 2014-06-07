package charliek.helloworld.resources

import charliek.helloworld.transfer.Hello
import com.charlieknudsen.dw.common.resources.AbstractResource
import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.databind.ObjectMapper

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path('/')
@Produces(MediaType.APPLICATION_JSON)
class BasicResource extends AbstractResource {

    BasicResource(ObjectMapper objectMapper) {
        super(objectMapper)
    }

    @GET
    @Timed
    Hello getIntroduction() {
        return new Hello(greeting: 'hello world')
    }

}
