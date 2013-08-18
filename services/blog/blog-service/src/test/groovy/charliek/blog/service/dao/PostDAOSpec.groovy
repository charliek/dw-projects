package charliek.blog.service.dao

import charliek.blog.service.BlogService
import charliek.blog.service.builders.PostBuilder
import charliek.blog.service.domain.PostEntity
import charliek.dw.test.DAOSpecification

class PostDAOSpec extends DAOSpecification<PostDAO> {

    @Override
    PostDAO buildDAO() {
        return new PostDAO(sessionFactory)
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
        PostEntity expectingNull = dao.findById(post.id+1)

        then:
        assert expectingPost.id == post.id
        assert expectingNull == null
    }

    def 'get a post by slug'() {
        given:
        PostEntity post = new PostBuilder(sessionFactory)
                .withSlug('test slug')
                .build()

        when:
        PostEntity expectingPost = dao.findBySlug('test slug')
        PostEntity expectingNull = dao.findBySlug('fake slug')

        then:
        assert expectingPost.id == post.id
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

}
