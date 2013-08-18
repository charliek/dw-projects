package charliek.blog.service.domain

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.joda.time.LocalDateTime

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.Lob
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = 'post')
@ToString(excludes = [])
@EqualsAndHashCode(excludes = [''])
class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = 'author_id')
    AuthorEntity author

    @Column(nullable = false)
    String title

    @Column(nullable = false)
    String slug

    @Lob
    @Column(nullable = false, columnDefinition='TEXT')
    String body

    @Column(nullable = false)
    Boolean draft

    @Column(name = 'date_published', nullable = true)
    LocalDateTime datePublished

    @Column(name = 'date_created', nullable = true)
    LocalDateTime dateCreated

    @Column(name = 'last_updated', nullable = true)
    LocalDateTime lastUpdated
}