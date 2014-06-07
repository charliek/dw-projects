package charliek.blog.service.builders

import charliek.blog.service.domain.AuthorEntity
import charliek.blog.service.domain.PostEntity
import org.hibernate.SessionFactory
import org.joda.time.LocalDateTime

class PostBuilder {

    SessionFactory sessionFactory
    AuthorEntity author
    PostEntity post

    PostBuilder(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory
        this.post = new PostEntity()
        setDefaults()
    }

    void setDefaults() {
        post.with {
            title = ''
            slug = ''
            body = ''
            draft = ''
            datePublished = null
            dateCreated = null
            lastUpdated = null
        }
    }

    PostBuilder withAuthor(AuthorEntity author) {
        this.author = author
        return this
    }

    PostBuilder withPublishDate(LocalDateTime dt) {
        post.datePublished = dt
        return this
    }

    PostBuilder withSlug(String slug) {
        post.slug = slug
        return this
    }

    PostEntity build(boolean save = true) {
        if (author == null) {
            author = new AuthorBuilder(sessionFactory).build()
        }
        post.author = author

        if (save) {
            sessionFactory.currentSession.save(post)
        }
        return post
    }


}
