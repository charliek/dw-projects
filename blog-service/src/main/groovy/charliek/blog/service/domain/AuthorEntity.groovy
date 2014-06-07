package charliek.blog.service.domain

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.joda.time.LocalDateTime

import javax.persistence.*

@Entity
@Table(name = 'author')
@ToString(excludes = [])
@EqualsAndHashCode(excludes = [''])
class AuthorEntity {

    // TODO add versions

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Column(nullable = false)
    String name

    @Column(name = 'github_user', nullable = false)
    String githubUser

    @Column(name = 'date_created', nullable = true)
    LocalDateTime dateCreated

    @Column(name = 'last_updated', nullable = true)
    LocalDateTime lastUpdated
}
