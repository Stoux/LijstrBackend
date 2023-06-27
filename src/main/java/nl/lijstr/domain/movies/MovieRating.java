package nl.lijstr.domain.movies;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.lijstr.domain.base.IdCmUserMovieModel;
import nl.lijstr.domain.users.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;

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
    @Column(precision = 3, scale = 1)
    private BigDecimal rating;
    @Column(length = 5000)
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

        @JsonValue
        public int toValue() {
            return ordinal();
        }

    }

    /**
     * Check if this rating has a comment.
     *
     * @return has comment
     */
    public boolean hasComment() {
        return !ObjectUtils.isEmpty(this.comment);
    }

}
