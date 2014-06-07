package charliek.blog.transfer

import org.joda.time.LocalDateTime

class Post {

    Long id
    Author author
    String title
    String slug
    String body
    Boolean draft
    LocalDateTime datePublished
    LocalDateTime dateCreated
    LocalDateTime lastUpdated
}
