package charliek.blog.service.dao

import charliek.blog.service.domain.AuthorEntity
import charliek.blog.service.domain.PostEntity
import com.charlieknudsen.dw.common.exceptions.Validate
import com.yammer.dropwizard.hibernate.AbstractDAO
import io.dropwizard.hibernate.AbstractDAO
import org.hibernate.Criteria
import org.hibernate.SessionFactory
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions
import org.joda.time.LocalDateTime

class PostDAO extends AbstractDAO<PostEntity> {

    private final AuthorDAO authorDAO

    public PostDAO(SessionFactory factory, authorDAO) {
        super(factory)
        this.authorDAO = authorDAO
    }

    public PostEntity findById(Long id) {
        return get(id)
    }

    @SuppressWarnings('ParameterReassignment')
    public PostEntity saveOrUpdate(PostEntity post) {
        LocalDateTime now = new LocalDateTime()
        if (post.id == null) {
            post.dateCreated = now
            validateNewPost(post)
        } else {
            validatePostUpdate(post)
            post = currentSession().merge(post) as PostEntity
        }
        post.lastUpdated = now
        return persist(post)
    }

    private void validatePostUpdate(PostEntity post) {
        Long authorId = post.author?.id
        Validate.isNotNull(authorId, 'Author is required')
        PostEntity pastPost = findById(post.id)
        Validate.isNotNull(pastPost, 'Invalid post id sent')
        Validate.match(authorId, pastPost.author.id, 'Author can not change on post')
        validateUniqueSlug(post, authorId)

        // Setup defaults that can not be updated
        post.author = pastPost.author
        post.dateCreated = pastPost.dateCreated
        post.datePublished = pastPost.datePublished
        if (! post.draft && ( pastPost.draft || post.datePublished == null )) {
            post.datePublished = new LocalDateTime()
        }
    }

    private void validateUniqueSlug(PostEntity post, Long authorId) {
        PostEntity lastPost = uniqueResult(criteria()
                .add(Restrictions.eq('slug', post.slug))
                .add(Restrictions.eq('author.id', authorId))
                .add(Restrictions.ne('id', post.id)))
        Validate.isNull(lastPost, 'Post slug is already used')
    }

    private void validateNewPost(PostEntity post) {
        Long authorId = post.author?.id
        Validate.isNotNull(authorId, 'Author is required')
        AuthorEntity author = authorDAO.findById(authorId)
        Validate.isNotNull(author, 'Passed in author not found')
        Validate.isNull(findBySlug(authorId, post.slug), 'Post slug is already used')

        // Don't let the user modify the author object
        post.author = author
        if (post.draft) {
            post.datePublished = null
        } else {
            post.datePublished = new LocalDateTime()
        }
    }

    public PostEntity findBySlug(Long authorId, String slug) {
        return uniqueResult(criteria()
                .add(Restrictions.eq('slug', slug))
                .add(Restrictions.eq('author.id', authorId))
        )
    }

    // TODO add ability to filter by drafts
    // Probably move toward a passed in search object
    public List<PostEntity> listPosts(int offset = 0, int maxResults = 10) {
        return paged(offset, maxResults, criteria()
                .addOrder(Order.desc('datePublished'))
                .addOrder(Order.desc('dateCreated'))
        )
    }

    // TODO add ability to filter by drafts
    // Probably move toward a passed in search object
    public List<PostEntity> listPostsByAuthor(Long authorId, int offset = 0, int maxResults = 10) {
        return paged(offset, maxResults, criteria()
                .add(Restrictions.eq('author.id', authorId))
                .addOrder(Order.desc('datePublished'))
                .addOrder(Order.desc('dateCreated'))
        )
    }

    private List<PostEntity> paged(int offset, int maxResults, Criteria criteria) {
        return list(criteria
                .setFirstResult(offset)
                .setMaxResults(maxResults)
        )
    }
}
