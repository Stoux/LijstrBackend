package nl.lijstr.api.movies.people;

import nl.lijstr.api.abs.AbsMoviePersonService;
import nl.lijstr.domain.movies.people.MovieCharacter;
import nl.lijstr.repositories.movies.people.MovieCharacterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * An endpoint for fetching movie characters.
 */
@RestController
@RequestMapping(value = "/movies/characters")
public class MovieCharacterEndpoint extends AbsMoviePersonService<MovieCharacter> {

    @Autowired
    public MovieCharacterEndpoint(MovieCharacterRepository repository) {
        super(repository);
    }

}
