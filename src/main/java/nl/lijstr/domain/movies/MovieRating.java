package nl.lijstr.domain.movies;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.*;
import nl.lijstr.domain.base.IdCmUserMovieModel;
import nl.lijstr.domain.users.User;

/**
 * Created by Stoux on 03/12/2015.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MovieRating extends IdCmUserMovieModel {

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private Seen seen;
    @Column(precision = 2)
    private BigDecimal rating;
    private String comment;
    @Column(nullable = false)
    private Boolean latest;

    /**
     * Create a new rating for a movie.
     *
     * @param movie   The movie
     * @param user    The user
     * @param seen    Has seen the movie
     * @param rating  The actual rating
     * @param comment A optional comment
     */
    public MovieRating(Movie movie, User user, Seen seen, BigDecimal rating, String comment) {
        this.movie = movie;
        this.user = user;
        this.seen = seen;
        this.rating = rating;
        this.comment = comment;
        this.latest = true;
    }

    /**
     * Seen status.
     */
    public enum Seen {
        YES,
        NO,
        UNKNOWN;

        /**
         * Parse a {@link Boolean} into a {@link Seen} value.
         *
         * @param val The boolean
         *
         * @return the seen value
         */
        public static Seen fromBoolean(Boolean val) {
            if (Boolean.TRUE.equals(val)) {
                return YES;
            } else if (Boolean.FALSE.equals(val)) {
                return NO;
            } else {
                return UNKNOWN;
            }
        }

    }

}
