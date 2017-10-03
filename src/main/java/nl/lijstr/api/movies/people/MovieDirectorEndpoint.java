package nl.lijstr.api.movies.people;

import nl.lijstr.api.abs.AbsMoviePersonService;
import nl.lijstr.domain.movies.people.MovieDirector;
import nl.lijstr.repositories.movies.people.MovieDirectorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * An endpoint for fetching movie directors.
 */
@RestController
@RequestMapping(value = "/movies/directors")
public class MovieDirectorEndpoint extends AbsMoviePersonService<MovieDirector> {

    @Autowired
    public MovieDirectorEndpoint(MovieDirectorRepository repository) {
        super(repository);
    }

}
