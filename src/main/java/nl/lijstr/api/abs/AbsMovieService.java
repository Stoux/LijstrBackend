package nl.lijstr.api.abs;

import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.repositories.movies.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

/**
 * Created by Stoux on 20/04/2016.
 */
@Secured(Permission.MOVIE_USER)
public abstract class AbsMovieService extends AbsService {

    @Autowired
    protected MovieRepository movieRepository;

    /**
     * Find a {@link Movie}.
     *
     * @param id The ID of the movie
     *
     * @return the movie
     */
    protected Movie findMovie(Long id) {
        return findOne(movieRepository, id, "Movie");
    }

}
