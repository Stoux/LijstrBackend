package nl.lijstr.domain.movies;

import java.math.BigDecimal;
import javax.persistence.Entity;
import lombok.*;
import nl.lijstr.domain.base.RatingModel;
import nl.lijstr.domain.users.User;

/**
 * Created by Stoux on 03/12/2015.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class MovieRating extends RatingModel<Movie> {

    /**
     * Create a new rating for a movie.
     *
     * @param movie   The movie
     * @param user    The user
     * @param seen    Has seen the target
     * @param rating  The actual rating (can be null for ?)
     * @param comment An optional comment
     */
    public MovieRating(Movie movie, User user, Seen seen, BigDecimal rating, String comment) {
        super(user, movie, seen, rating, comment);
    }

}
