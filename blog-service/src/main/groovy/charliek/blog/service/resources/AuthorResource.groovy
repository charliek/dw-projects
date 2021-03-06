package charliek.blog.service.resources

import charliek.blog.service.dao.AuthorDAO
import charliek.blog.service.dao.PostDAO
import charliek.blog.service.domain.AuthorEntity
import charliek.blog.transfer.Author
import charliek.blog.transfer.Post
import com.charlieknudsen.dw.common.exceptions.ValidationException
import com.charlieknudsen.dw.common.resources.AbstractResource
import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.dropwizard.hibernate.UnitOfWork

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

    @Path('/{githubName}')
    @GET
    @UnitOfWork(transactional = true)
    @Timed
    Author getAuthorByGithubName(@PathParam('githubName') String githubName) {
        return translateAndReturn(authorDAO.findByGithubUser(githubName), Author)
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
