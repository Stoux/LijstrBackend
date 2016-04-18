package nl.lijstr.api.movies;

import java.util.List;
import java.util.stream.Collectors;
import nl.lijstr.api.abs.AbsRestService;
import nl.lijstr.api.movies.models.MovieSummary;
import nl.lijstr.api.movies.models.post.PostedMovieRequest;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.movies.MovieRequest;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.exceptions.BadRequestException;
import nl.lijstr.repositories.abs.BasicRepository;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.repositories.users.UserRepository;
import nl.lijstr.security.model.JwtUser;
import nl.lijstr.services.maf.MafApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * The Movies Endpoint.
 */
@RestController
@RequestMapping(value = "/movies", produces = "application/json")
public class MovieEndpoint extends AbsRestService<Movie> {

    @Autowired
    private MafApiService apiService;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new MovieEndpoint.
     */
    public MovieEndpoint() {
        super("Movie");
    }

    @Override
    protected BasicRepository<Movie> getRestRepository() {
        return movieRepository;
    }

    /**
     * Get a list of short versions of all movies.
     *
     * @param includeGenres    Should include genres
     * @param includeLanguages Should include languages
     * @param includeAgeRating Should include age rating
     * @return the list
     */
    @RequestMapping("/summaries")
    public List<MovieSummary> summaries(
            @RequestParam(required = false, defaultValue = "false") final boolean includeGenres,
            @RequestParam(required = false, defaultValue = "false") final boolean includeLanguages,
            @RequestParam(required = false, defaultValue = "false") final boolean includeAgeRating) {
        return movieRepository.findAll()
                .stream()
                .map(m -> MovieSummary.convert(m, includeGenres, includeLanguages, includeAgeRating))
                .collect(Collectors.toList());
    }

    /**
     * Request a Movie.
     *
     * @param postedRequest The data
     */
    @Secured(Permission.ROLE_MOVIE)
    @Transactional
    @RequestMapping(value = "/request", method = RequestMethod.POST)
    public void requestMovie(@RequestBody() PostedMovieRequest postedRequest) {
        JwtUser user = getUser();
        checkIfMovieNotAdded(postedRequest.getImdbId());
        checkIfMovieExists(postedRequest.getImdbId());
        MovieRequest request = new MovieRequest(postedRequest.getImdbId(), postedRequest.getYoutubeId());
        //TODO: Add to DB
    }

    /**
     * Add a new Movie to the DB.
     *
     * @param postedRequest The data
     */
    @Secured(Permission.ROLE_MOVIE_MOD)
    @Transactional
    @RequestMapping(method = RequestMethod.POST)
    public void addMovie(@RequestBody() PostedMovieRequest postedRequest) {
        //Validate
        JwtUser user = getUser();
        checkIfMovieNotAdded(postedRequest.getImdbId());
        checkIfMovieExists(postedRequest.getImdbId());

        //Add the movie
        Movie newMovie = new Movie(
                postedRequest.getImdbId(),
                postedRequest.getYoutubeId(),
                userRepository.findOne(user.getId())
        );
        final Movie movie = movieRepository.save(newMovie);
        apiService.updateMovie(movie);

    }

    private void checkIfMovieNotAdded(String imdbId) {
        if (movieRepository.findByImdbId(imdbId) != null) {
            throw new BadRequestException("Movie already added");
        }
    }

    private void checkIfMovieExists(String imdbId) {
        //TODO:
    }

}
