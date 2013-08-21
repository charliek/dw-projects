package charliek.blog.service.resources

import charliek.blog.service.BlogService
import charliek.blog.service.builders.AuthorBuilder
import charliek.blog.service.builders.PostBuilder
import charliek.blog.service.dao.AuthorDAO
import charliek.blog.service.dao.PostDAO
import charliek.blog.service.domain.AuthorEntity
import charliek.blog.service.domain.PostEntity
import charliek.blog.transfer.Author
import charliek.blog.transfer.Post
import charliek.dw.test.IntegrationSpecification
import com.fasterxml.jackson.databind.ObjectMapper
import com.sun.jersey.api.client.GenericType
import com.sun.jersey.api.client.UniformInterfaceException
import com.yammer.dropwizard.json.ObjectMapperFactory
import spock.lang.Shared

import javax.ws.rs.core.MediaType

class PostResourceSpec extends IntegrationSpecification {

    @Shared PostDAO postDAO
    @Shared AuthorDAO authorDAO
    @Shared ObjectMapper objectMapper = new ObjectMapperFactory().build()

    @Override
    void setupDomain() {
        authorDAO = new AuthorDAO(sessionFactory)
        postDAO = new PostDAO(sessionFactory, authorDAO)
    }

    @Override
    List<Class<?>> getEntities() {
        BlogService.SERVICE_ENTITIES
    }

    AuthorEntity authorEntity
    PostEntity postEntity

    def setup() {
        authorEntity = new AuthorBuilder(sessionFactory)
                .build()
        postEntity = new PostBuilder(sessionFactory)
                .withAuthor(authorEntity)
                .withSlug('test post')
                .build()
    }

    @Override
    void setupResources() {
        addResource(new PostResource(postDAO, objectMapper))
    }

    def 'call the /post/{id} endpoint with an invalid Id'() {
        given: 'an invalid Id'
        String invalidId = '1'

        when: 'requesting an invalid Id'
        request("/employers/${invalidId}", new GenericType<Post>() {})

        then: 'respond 404'
        UniformInterfaceException e = thrown(UniformInterfaceException)
        assert e.response.status == 404
    }

    def 'call the /post/{id} endpoint with a valid Id'() {
        when: 'requesting a valid Id'
        Post post = request("/post/${postEntity.id}", new GenericType<Post>() {})

        then: 'return the post transfer object'
        assert post
        assert post.slug == postEntity.slug
        assert post.author.id == authorEntity.id
    }

    private Post buildPost() {
        return objectMapper.convertValue(postEntity, Post)
    }

    def 'add post via the /post/ endpoint'() {
        when: 'requesting a valid Id'
        // TODO add validations around 422 errors
        Post sendPost = buildPost()
        sendPost.id = null
        sendPost.slug = 'unused slug'
        sendPost.body = 'test'
        Post returnedAuthor = client.resource("/post")
                .entity(sendPost, MediaType.APPLICATION_JSON_TYPE)
                .post(new GenericType<Post>() {})

        then: 'return the post transfer object'
        assert returnedAuthor.id != null
        assert returnedAuthor.slug == sendPost.slug
        assert returnedAuthor.body == sendPost.body

        and:
        assert postEntity.id != returnedAuthor.id
        PostEntity entity = postDAO.findById(returnedAuthor.id)
        assert entity.slug == sendPost.slug
        assert entity.body == sendPost.body
    }

    def 'update post via the /post/{id} endpoint'() {
        when: 'requesting a valid Id'
        // TODO add validations around 422 errors
        Post sendPost = buildPost()
        sendPost.slug = 'unused slug2'
        sendPost.body = 'test'
        Post returnedAuthor = client.resource("/post/${postEntity.id}")
                .entity(sendPost, MediaType.APPLICATION_JSON_TYPE)
                .put(new GenericType<Post>() {})

        then: 'return the post transfer object'
        assert returnedAuthor.slug == sendPost.slug
        assert returnedAuthor.body == sendPost.body

        and:
        assert postEntity.id == returnedAuthor.id
        PostEntity entity = postDAO.findById(sendPost.id)
        assert entity.slug == sendPost.slug
        assert entity.body == sendPost.body
    }
}
