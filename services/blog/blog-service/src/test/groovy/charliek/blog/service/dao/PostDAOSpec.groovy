package charliek.blog.service.dao

import charliek.blog.service.BlogService
import charliek.blog.service.builders.AuthorBuilder
import charliek.blog.service.builders.PostBuilder
import charliek.blog.service.domain.AuthorEntity
import charliek.blog.service.domain.PostEntity
import charliek.dw.test.DAOSpecification
import org.joda.time.LocalDateTime

class PostDAOSpec extends DAOSpecification<PostDAO> {

    @Override
    PostDAO buildDAO() {
        return new PostDAO(sessionFactory, new AuthorDAO(sessionFactory))
    }

    @Override
    List<Class<?>> getEntities() {
        return BlogService.SERVICE_ENTITIES
    }

    def 'get a post by id'() {
        given:
        PostEntity post = new PostBuilder(sessionFactory).build()

        when:
        PostEntity expectingPost = dao.findById(post.id)
        PostEntity expectingNull = dao.findById(post.id + 1)

        then:
        assert expectingPost.id == post.id
        assert expectingNull == null
    }

    def 'get a post by slug'() {
        given:
        AuthorEntity author = new AuthorBuilder(sessionFactory).build()
        PostEntity post = new PostBuilder(sessionFactory)
                .withSlug('test slug')
                .withAuthor(author)
                .build()

        when:
        PostEntity expectingPost = dao.findBySlug(author.id, 'test slug')
        PostEntity expectingNull = dao.findBySlug(author.id, 'fake slug')

        then:
        assert expectingPost.id == post.id
        assert expectingPost.author.id == author.id
        assert expectingNull == null
    }

    def 'time set when saving post'() {
        given:
        PostEntity post = new PostBuilder(sessionFactory)
                .build(false)

        when:
        PostEntity expectingPost = dao.saveOrUpdate(post)

        then:
        assert expectingPost.id == post.id
        assert expectingPost.dateCreated != null
        assert expectingPost.lastUpdated != null
    }

    def 'list posts by author'() {
        given: 'setup 3 posts by the same author'
        LocalDateTime dt = new LocalDateTime()
        AuthorEntity author = new AuthorBuilder(sessionFactory).build()
        PostEntity post1 = new PostBuilder(sessionFactory)
                .withSlug('test slug')
                .withPublishDate(dt.plusDays(2))
                .withAuthor(author)
                .build()
        PostEntity post2 = new PostBuilder(sessionFactory)
                .withSlug('test slug')
                .withPublishDate(dt.plusDays(1))
                .withAuthor(author)
                .build()
        PostEntity post3 = new PostBuilder(sessionFactory)
                .withSlug('test slug')
                .withPublishDate(dt)
                .withAuthor(author)
                .build()

        when: 'grab the first 2'
        List<PostEntity> posts1 = dao.listPostsByAuthor(author.id, 0, 2)

        then: 'we have the first 2 posts'
        assert posts1.size() == 2
        assert posts1[0].id == post1.id
        assert posts1[1].id == post2.id

        when: 'grab everything after the second'
        List<PostEntity> posts2 = dao.listPostsByAuthor(author.id, 2, 100)

        then: 'we only get the third'
        assert posts2.size() == 1
        assert posts2[0].id == post3.id
    }

    def 'list posts'() {
        given: 'setup 3 posts by the same author'
        LocalDateTime dt = new LocalDateTime()
        AuthorEntity author = new AuthorBuilder(sessionFactory).build()
        PostEntity post1 = new PostBuilder(sessionFactory)
                .withSlug('test slug')
                .withPublishDate(dt)
                .withAuthor(author)
                .build()

        when: 'grab the first 2'
        List<PostEntity> posts1 = dao.listPostsByAuthor(author.id, 0, 2)

        then: 'we have the first post'
        assert posts1.size() == 1
        assert posts1[0].id == post1.id
    }

    // TODO write test for save scenarios
    def 'validate author is required on update'() {

    }

    def 'validate post id on update'() {

    }

    def 'validate no post id on create'() {

    }

    def 'validate author can not change on update'() {

    }

    def 'validate post slug has been used on save'() {

    }

    def 'validate post slug has been used on update'() {

    }

    def 'validate publish date set on non-draft save'() {

    }

    def 'validate publish date unset on draft save'() {

    }

}
