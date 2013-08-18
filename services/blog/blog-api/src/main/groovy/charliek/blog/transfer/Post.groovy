package charliek.blog.transfer

import org.joda.time.LocalDate

class Post {

    Long id
    Author author
    String title
    String slug
    String body
    Boolean draft
    LocalDate datePublished
    LocalDate dateCreated
    LocalDate lastUpdated
}
