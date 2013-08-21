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
        author.name = ''
        author.githubUser = ''
        author.dateCreated = new LocalDateTime()
        author.lastUpdated = new LocalDateTime()
    }

    AuthorBuilder withGithubUser(String githubUser) {
        author.githubUser = githubUser
        return this
    }

    AuthorEntity build() {
        sessionFactory.currentSession.save(author)
        return author
    }
}
