package charliek.blog.service.resources

import charliek.blog.service.dao.PostDAO
import charliek.blog.transfer.Post
import charliek.dw.resources.AbstractResource
import com.fasterxml.jackson.databind.ObjectMapper
import com.yammer.dropwizard.hibernate.UnitOfWork
import com.yammer.metrics.annotation.Timed

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path('/post')
@Produces(MediaType.APPLICATION_JSON)
class PostResource extends AbstractResource {

    private final PostDAO postDAO

    PostResource(PostDAO postDAO, ObjectMapper objectMapper) {
        super(objectMapper)
        this.postDAO = postDAO
    }

    @Path('/{id}')
    @GET
    @UnitOfWork(transactional = false)
    @Timed
    Post getPost(@PathParam('id') Long id) {
      return translateAndReturn(postDAO.findById(id), Post)
    }

}
