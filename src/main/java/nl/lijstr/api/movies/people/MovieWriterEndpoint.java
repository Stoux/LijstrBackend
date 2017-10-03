package nl.lijstr.api.movies.people;

import nl.lijstr.api.abs.AbsMoviePersonService;
import nl.lijstr.domain.movies.people.MovieWriter;
import nl.lijstr.repositories.movies.people.MovieWriterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * An endpoint for fetching movie writers.
 */
@RestController
@RequestMapping(value = "/movies/writers")
public class MovieWriterEndpoint extends AbsMoviePersonService<MovieWriter> {

    @Autowired
    public MovieWriterEndpoint(MovieWriterRepository repository) {
        super(repository);
    }

}
