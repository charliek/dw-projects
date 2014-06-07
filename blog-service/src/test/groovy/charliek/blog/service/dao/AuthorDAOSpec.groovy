package charliek.blog.service.dao

import charliek.blog.service.BlogService
import charliek.blog.service.builders.AuthorBuilder
import charliek.blog.service.domain.AuthorEntity
import com.charlieknudsen.dw.common.exceptions.ValidationException
import com.charlieknudsen.dw.test.DAOSpecification

class AuthorDAOSpec extends DAOSpecification<AuthorDAO> {

    AuthorEntity author1
    AuthorEntity author2

    @Override
    AuthorDAO buildDAO() {
        return new AuthorDAO(sessionFactory)
    }

    @Override
    List<Class<?>> getEntities() {
        return BlogService.SERVICE_ENTITIES
    }

    def setup() {
        author1 = new AuthorBuilder(sessionFactory)
                .withGithubUser('bob')
                .build()
        author2 = new AuthorBuilder(sessionFactory)
                .withGithubUser('bill')
                .build()
    }

    def 'get an author by id'() {
        when:
        AuthorEntity expectingAuthor = dao.findById(author2.id)
        AuthorEntity expectingNull = dao.findById(author2.id + 1)

        then:
        assert expectingAuthor.id == author2.id
        assert expectingNull == null
    }

    def 'get an author by githubUser'() {
        when:
        AuthorEntity expectingAuthor = dao.findByGithubUser(author2.githubUser)
        AuthorEntity expectingNull = dao.findByGithubUser('fake')

        then:
        assert expectingAuthor.id == author2.id
        assert expectingNull == null
    }

    def 'add a user with a duplicate username'() {
        when:
        dao.saveOrUpdate(new AuthorEntity(githubUser: 'bob'))

        then:
        def e = thrown(ValidationException)
        assert e.message.contains('already registered')
    }

    def 'add a user'() {
        when:
        AuthorEntity author = dao.saveOrUpdate(new AuthorEntity(githubUser: 'h', name: 'i'))

        then:
        assert author.id != null
        assert author.githubUser == 'h'
        assert author.name == 'i'
        assert author.dateCreated != null
        assert author.lastUpdated != null
    }

    def 'update a user to a duplicate username'() {
        when:
        author2.githubUser = author1.githubUser
        dao.saveOrUpdate(author2)

        then:
        def e = thrown(ValidationException)
        assert e.message.contains('already registered')
    }

    def 'update a user'() {
        when:
        author2.githubUser = 'ted'
        author2.name = 'ted test'
        dao.saveOrUpdate(author2)

        then:
        author2.githubUser == 'ted'
        author2.name == 'ted test'
    }

}
