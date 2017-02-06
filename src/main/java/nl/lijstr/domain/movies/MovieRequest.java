package nl.lijstr.domain.movies;

import java.math.BigDecimal;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.lijstr.domain.base.IdCmUserModel;
import nl.lijstr.domain.users.User;

/**
 * Created by Stoux on 03/12/2015.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class MovieRequest extends IdCmUserModel {

    @Column(nullable = false)
    private String imdbId;
    private String youtubeUrl;

    private String title;
    private String year;
    private String imdbRating;

    //Duplicate of MovieRating, not the most ideal solution..
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private MovieRating.Seen seen;
    @Column(precision = 2, scale = 1)
    private BigDecimal rating;
    private String comment;

    @ManyToOne
    private User approvedBy;

    /**
     * Create a new MovieRequest.
     *
     * @param user       The user requesting the movie
     * @param imdbId     The IMDB ID
     * @param youtubeUrl An optional YouTube URL
     * @param title      The title
     * @param year       The release year
     * @param imdbRating The rating
     * @param seen       The user's seen status
     * @param rating     The user's rating
     * @param comment    The user's comment (on their rating)
     */
    public MovieRequest(User user, String imdbId, String youtubeUrl, String title, String year, String imdbRating, MovieRating.Seen seen, BigDecimal rating, String comment) {
        this.setUser(user);
        this.imdbId = imdbId;
        this.youtubeUrl = youtubeUrl;
        this.title = title;
        this.year = year;
        this.imdbRating = imdbRating;
        this.seen = seen;
        this.rating = rating;
        this.comment = comment;
    }
}
