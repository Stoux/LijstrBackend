package nl.lijstr.api.movies;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import nl.lijstr.api.abs.AbsMovieService;
import nl.lijstr.api.movies.models.MovieUserMetaData;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.movies.MovieUserMeta;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.domain.users.User;
import nl.lijstr.repositories.movies.MovieUserMetaRepository;
import nl.lijstr.security.model.JwtUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoint for user's meta data on a movie.
 */
@Secured(Permission.MOVIE_USER)
@RestController
@RequestMapping(value = "/movies", produces = "application/json")
public class MovieMetaEndpoint extends AbsMovieService {

    @Autowired
    private MovieUserMetaRepository metaRepository;

    /**
     * Get the meta data a user has for a movie.
     *
     * @param movieId The movie ID
     *
     * @return the meta data
     */
    @RequestMapping(path = "/{movieId:\\d+}/meta", method = RequestMethod.GET)
    public MovieUserMetaData getForUser(@PathVariable() long movieId) {
        JwtUser user = getUser();
        Movie movie = findMovie(movieId);

        MovieUserMeta userMeta = metaRepository.findByMovieAndUser(movie, new User(user.getId()));
        if (userMeta == null) {
            return MovieUserMetaData.withDefaultValues();
        } else {
            return MovieUserMetaData.fromDomain(userMeta);
        }
    }

    /**
     * Update the meta data for a user.
     *
     * @param movieId  The movieId
     * @param metaData The new meta data
     */
    @RequestMapping(path = "/{movieId:\\d+}/meta", method = RequestMethod.PUT)
    public void update(@PathVariable() long movieId, @Valid @RequestBody MovieUserMetaData metaData) {
        User user = new User(getUser().getId());
        Movie movie = findMovie(movieId);

        //Check if one already exists
        MovieUserMeta userMeta = metaRepository.findByMovieAndUser(movie, user);
        if (userMeta != null) {
            metaData.update(userMeta);
            metaRepository.saveAndFlush(userMeta);
        } else {
            MovieUserMeta meta = new MovieUserMeta(user, movie, metaData.isWantToWatch());
            metaRepository.saveAndFlush(meta);
        }
    }

    /**
     * Get all the movies a user want's to watch.
     *
     * @return ID of the movies
     */
    @RequestMapping(path = "/wantToWatch", method = RequestMethod.GET)
    public List<Long> getWantToWatchMovies() {
        User user = new User(getUser().getId());
        return metaRepository.findByUserAndWantToWatch(user, true).stream()
                .map(meta -> meta.getMovie().getId())
                .collect(Collectors.toList());
    }


}
