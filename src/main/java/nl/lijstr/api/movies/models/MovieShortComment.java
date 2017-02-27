package nl.lijstr.api.movies.models;

import java.time.LocalDateTime;
import javax.persistence.Column;
import lombok.Getter;
import nl.lijstr.domain.movies.MovieComment;

/**
 * A representable version of a movie comment.
 */
@Getter
public class MovieShortComment implements TimeBased {

    private long id;

    private LocalDateTime lastModified;
    private LocalDateTime created;

    private long user;
    @Column(length = 5000)
    private String comment;

    /**
     * Create a {@link MovieShortComment} based on a Domain {@link MovieComment}.
     *
     * @param movieComment The original MovieComment
     */
    public MovieShortComment(MovieComment movieComment) {
        this.id = movieComment.getId();
        this.lastModified = movieComment.getLastModified();
        this.created = movieComment.getCreated();
        this.user = movieComment.getUser().getId();
        this.comment = movieComment.getComment();
    }
}
