package charliek.blog.service.dao

import charliek.blog.service.domain.PostEntity
import com.yammer.dropwizard.hibernate.AbstractDAO
import org.hibernate.SessionFactory
import org.hibernate.criterion.Restrictions
import org.joda.time.LocalDateTime

class PostDAO extends AbstractDAO<PostEntity> {

    public PostDAO(SessionFactory factory) {
        super(factory)
    }

    public PostEntity findById(Long id) {
        return get(id)
    }

    public PostEntity saveOrUpdate(PostEntity post) {
        LocalDateTime now = new LocalDateTime()
        if (post.id == null) {
            post.dateCreated = now
        }
        post.lastUpdated = now
        return persist(post)
    }

    public PostEntity findBySlug(String slug) {
        return uniqueResult(criteria().add(Restrictions.eq('slug', slug)))
    }
}
