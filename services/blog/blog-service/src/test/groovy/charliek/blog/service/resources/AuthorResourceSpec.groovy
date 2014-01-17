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
import com.charlieknudsen.dw.test.IntegrationSpecification
import com.sun.jersey.api.client.GenericType
import com.yammer.dropwizard.json.ObjectMapperFactory
import spock.lang.Shared

import javax.ws.rs.core.MediaType

class AuthorResourceSpec extends IntegrationSpecification {

    @Shared PostDAO postDAO
    @Shared AuthorDAO authorDAO

    @Override
    void setupDomain() {
        authorDAO = new AuthorDAO(sessionFactory)
        postDAO = new PostDAO(sessionFactory, authorDAO)
    }

    @Override
    List<Class<?>> getEntities() {
        BlogService.SERVICE_ENTITIES
    }

    @Override
    void setupResources() {
        addResource(new AuthorResource(postDAO, authorDAO, new ObjectMapperFactory().build()))
    }

    AuthorEntity authorEntity
    PostEntity postEntity

    def setup() {
        authorEntity = new AuthorBuilder(sessionFactory)
                .build()
        postEntity = new PostBuilder(sessionFactory)
                .withAuthor(authorEntity)
                .withSlug('test-post')
                .build()
    }

    def 'call the /author/{id}/post endpoint with a valid Id'() {
        when: 'requesting a valid Id'
        List<Post> posts = request("/author/${authorEntity.id}/post", new GenericType<List<Post>>() {})

        then: 'return the post transfer object'
        assert posts.size() == 1
        assert posts[0].slug == postEntity.slug
        assert posts[0].author.id == authorEntity.id
    }

    def 'call the /author/{id}/post/{slug} endpoint with a valid Id'() {
        when: 'requesting a valid Id'
        Post post = request(
                "/author/${authorEntity.id}/post/${postEntity.slug}",
                new GenericType<Post>() {})

        then: 'return the post transfer object'
        assert post.slug == postEntity.slug
        assert post.author.id == authorEntity.id
    }

    def 'add author via the /author/ endpoint'() {
        when: 'requesting a valid Id'
        // TODO add validations around 422 errors
        Author sendAuthor = new Author(name: 't', githubUser: 'x')
        Author returnedAuthor = client.resource("/author")
                .entity(sendAuthor, MediaType.APPLICATION_JSON_TYPE)
                .post(new GenericType<Author>() {})

        then: 'return the post transfer object'
        assert returnedAuthor.id != null
        assert returnedAuthor.name == sendAuthor.name
        assert returnedAuthor.githubUser == sendAuthor.githubUser

        and:
        AuthorEntity entity = authorDAO.findById(returnedAuthor.id)
        assert entity.name == returnedAuthor.name
        assert entity.githubUser == returnedAuthor.githubUser
    }

    def 'update author via the /author/{id} endpoint'() {
        when: 'requesting a valid Id'
        // TODO add validations around 422 errors
        Author sendAuthor = new Author(id: authorEntity.id, name: 't', githubUser: 'x')
        Author returnedAuthor = client.resource("/author/${authorEntity.id}")
                .entity(sendAuthor, MediaType.APPLICATION_JSON_TYPE)
                .put(new GenericType<Author>() {})

        then: 'return the post transfer object'
        assert returnedAuthor.id == sendAuthor.id
        assert returnedAuthor.name == sendAuthor.name
        assert returnedAuthor.githubUser == sendAuthor.githubUser

        and:
        def entity = authorDAO.findById(sendAuthor.id)
        assert entity.name == sendAuthor.name
        assert entity.githubUser == sendAuthor.githubUser
        assert entity.id == returnedAuthor.id
    }

}
