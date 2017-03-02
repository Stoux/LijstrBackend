package nl.lijstr.api.movies.models;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.users.User;

/**
 * Model with movie information.
 */
@Getter
@AllArgsConstructor
public class MovieAddedDetail {

    private long id;
    private String title;
    private LocalDateTime created;
    private Long addedBy;

    /**
     * Create a Added detail from a {@link Movie}.
     * @param movie The movie
     * @return the added detial
     */
    public static MovieAddedDetail fromMovie(Movie movie) {
        User addedBy = movie.getAddedBy();
        return new MovieAddedDetail(
                movie.getId(), movie.getTitle(), movie.getCreated(), addedBy == null ? null : addedBy.getId()
        );
    }


}
