package charliek.blog.service.resources

import charliek.blog.service.dao.AuthorDAO
import charliek.blog.service.dao.PostDAO
import charliek.blog.service.domain.AuthorEntity
import charliek.blog.transfer.Author
import charliek.blog.transfer.Post
import charliek.dw.exceptions.ValidationException
import charliek.dw.resources.AbstractResource
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.yammer.dropwizard.hibernate.UnitOfWork
import com.yammer.metrics.annotation.Timed

import javax.validation.Valid
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path('/author')
@Produces(MediaType.APPLICATION_JSON)
class AuthorResource extends AbstractResource {

    private final PostDAO postDAO
    private final AuthorDAO authorDAO

    AuthorResource(PostDAO postDAO, AuthorDAO authorDAO, ObjectMapper objectMapper) {
        super(objectMapper)
        this.postDAO = postDAO
        this.authorDAO = authorDAO
    }

    @Path('/{id}')
    @PUT
    @UnitOfWork(transactional = true)
    @Timed
    Author updateAuthor(@PathParam('id') Long id, @Valid Author author) {
        if (id != author.id) {
            throw new ValidationException('Id mismatch in request')
        }
        AuthorEntity entity = convert(author, AuthorEntity)
        return translateAndReturn(authorDAO.saveOrUpdate(entity), Author)
    }

    @POST
    @UnitOfWork(transactional = true)
    @Timed
    Author addAuthor(@Valid Author author) {
        AuthorEntity entity = convert(author, AuthorEntity)
        return translateAndReturn(authorDAO.saveOrUpdate(entity), Author)
    }

    @Path('/{id}/post')
    @GET
    @UnitOfWork(transactional = false)
    @Timed
    List<Post> getPostsByAuthor(@PathParam('id') Long id) {
        return translateAndReturn(postDAO.listPostsByAuthor(id), new TypeReference<List<Post>>() {})
    }

    @Path('/{id}/post/{slug}')
    @GET
    @UnitOfWork(transactional = false)
    @Timed
    Post getPostBySlug(@PathParam('id') Long id, @PathParam('slug') String slug) {
        return translateAndReturn(postDAO.findBySlug(id, slug), Post)
    }

}
