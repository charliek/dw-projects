package charliek.blog.service.dao

import charliek.blog.service.domain.AuthorEntity
import com.charlieknudsen.dw.common.exceptions.ValidationException
import com.google.common.base.Preconditions
import io.dropwizard.hibernate.AbstractDAO
import org.hibernate.SessionFactory
import org.hibernate.criterion.Restrictions
import org.joda.time.LocalDateTime

class AuthorDAO extends AbstractDAO<AuthorEntity> {

    public AuthorDAO(SessionFactory factory) {
        super(factory)
    }

    public AuthorEntity findById(Long id) {
        return get(id)
    }

    @SuppressWarnings('ParameterReassignment')
    public AuthorEntity saveOrUpdate(AuthorEntity author) {
        LocalDateTime now = new LocalDateTime()
        if (author.id == null) {
            author.dateCreated = now
            validateNewAuthor(author)
        } else {
            validateAuthorUpdate(author)
            author = currentSession().merge(author) as AuthorEntity
        }
        author.lastUpdated = now
        return persist(author)
    }

    private void validateNewAuthor(AuthorEntity author) {
        if (findByGithubUser(author.githubUser) != null) {
            throw new ValidationException('username already registered')
        }
    }

    private void validateUniqueGithubUser(String githubUser, Long id) {
        AuthorEntity author = uniqueResult(criteria()
                .add(Restrictions.ne('id', id))
                .add(Restrictions.eq('githubUser', githubUser)))
        if (author != null) {
            throw new ValidationException('username already registered')
        }
    }

    private void validateAuthorUpdate(AuthorEntity author) {
        // use a finder so we always pull from the database
        Preconditions.checkNotNull(author.id)
        AuthorEntity currentAuthor = findById(author.id)
        if (currentAuthor == null) {
            throw new ValidationException('author not found')
        } else {
            validateUniqueGithubUser(author.githubUser, author.id)
        }
    }

    public AuthorEntity findByGithubUser(String githubUser) {
        return uniqueResult(criteria()
                .add(Restrictions.eq('githubUser', githubUser))
        )
    }

}
