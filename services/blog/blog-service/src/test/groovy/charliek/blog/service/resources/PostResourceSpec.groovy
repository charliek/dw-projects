package charliek.blog.service.resources

import charliek.blog.service.BlogService
import charliek.blog.service.builders.AuthorBuilder
import charliek.blog.service.builders.PostBuilder
import charliek.blog.service.dao.PostDAO
import charliek.blog.service.domain.AuthorEntity
import charliek.blog.service.domain.PostEntity
import charliek.blog.transfer.Post
import charliek.dw.test.IntegrationSpecification
import com.sun.jersey.api.client.GenericType
import com.sun.jersey.api.client.UniformInterfaceException
import com.yammer.dropwizard.json.ObjectMapperFactory
import spock.lang.Shared

class PostResourceSpec extends IntegrationSpecification  {

    @Shared PostDAO postDAO

    @Override
    void setupDomain() {
        postDAO = new PostDAO(sessionFactory)
    }

    @Override
    List<Class<?>> getEntities() {
        BlogService.SERVICE_ENTITIES
    }

    @Override
    void setupResources() {
        addResource(new PostResource(postDAO, new ObjectMapperFactory().build()))
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
        given: 'setup a post'
        AuthorEntity authorEntity = new AuthorBuilder(sessionFactory)
                .build()
        PostEntity postEntity = new PostBuilder(sessionFactory)
                .withAuthor(authorEntity)
                .withSlug('test post')
                .build()

        when: 'requesting a valid Id'
        Post post = request("/post/${postEntity.id}", new GenericType<Post>() {})

        then: 'return the employer transfer object'
        assert post
        assert post.slug == postEntity.slug
        assert post.author.id == authorEntity.id
    }
}
