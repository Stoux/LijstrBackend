package nl.lijstr.api.movies.models.wrappers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nl.lijstr.api.movies.models.MovieShortDetail;

/**
 * Model class that contains info about a recent change regarding a movie.
 *
 * @param <X> The type of change
 */
@Getter
@AllArgsConstructor
public class MovieRecentChange<X> {

    private MovieShortDetail movie;
    private X change;

}
