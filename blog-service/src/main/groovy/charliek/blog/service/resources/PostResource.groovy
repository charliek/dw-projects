package charliek.blog.service.resources

import charliek.blog.service.dao.PostDAO
import charliek.blog.service.domain.PostEntity
import charliek.blog.transfer.Post
import com.charlieknudsen.dw.common.exceptions.ValidationException
import com.charlieknudsen.dw.common.resources.AbstractResource
import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.dropwizard.hibernate.UnitOfWork

import javax.validation.Valid
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.PUT
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
    Post getPostById(@PathParam('id') Long id) {
        return translateAndReturn(postDAO.findById(id), Post)
    }

    @GET
    @UnitOfWork(transactional = false)
    @Timed
    List<Post> getAllPosts() {
        return translateAndReturn(postDAO.listPosts(), new TypeReference<List<Post>>() {})
    }

    @POST
    @UnitOfWork(transactional = true)
    @Timed
    Post addPost(@Valid Post post) {
        PostEntity entity = convert(post, PostEntity)
        return translateAndReturn(postDAO.saveOrUpdate(entity), Post)
    }

    @Path('/{id}')
    @PUT
    @UnitOfWork(transactional = true)
    @Timed
    Post updatePost(@PathParam('id') Long id, @Valid Post post) {
        if (id != post.id) {
            throw new ValidationException('Id mismatch in request')
        }
        PostEntity entity = convert(post, PostEntity)
        return translateAndReturn(postDAO.saveOrUpdate(entity), Post)
    }

}
