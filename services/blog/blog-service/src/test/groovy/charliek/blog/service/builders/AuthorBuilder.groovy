package charliek.blog.service.builders

import charliek.blog.service.domain.AuthorEntity
import org.hibernate.SessionFactory
import org.joda.time.LocalDateTime

class AuthorBuilder {

    SessionFactory sessionFactory
    AuthorEntity author

    AuthorBuilder(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory
        author = new AuthorEntity()
        setDefaults()
    }

    void setDefaults() {

    }

    AuthorEntity build() {
        author.name = ''
        author.githubUser = ''
        author.dateCreated = new LocalDateTime()
        author.lastUpdated = new LocalDateTime()
        sessionFactory.currentSession.save(author)
        return author
    }
}
