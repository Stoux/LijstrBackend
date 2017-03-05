package nl.lijstr.api.movies;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import nl.lijstr.api.abs.AbsService;
import nl.lijstr.api.movies.models.MovieDetail;
import nl.lijstr.api.movies.models.MovieSummary;
import nl.lijstr.api.movies.models.post.PostedMovieRequest;
import nl.lijstr.beans.MovieAddBean;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.domain.users.User;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.security.model.JwtUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * The Movies Endpoint.
 */
@RestController
@RequestMapping(value = "/movies", produces = "application/json")
public class MovieEndpoint extends AbsService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieAddBean movieAddBean;

    /**
     * Get a {@link Movie} as detail view.
     *
     * @param id The ID of the movie
     *
     * @return the movie detail
     */
    @RequestMapping("/{id}")
    public MovieDetail getById(@PathVariable("id") final long id) {
        Movie movie = findOne(movieRepository, id, "movie");
        return MovieDetail.fromMovie(movie);
    }

    /**
     * Get the original {@link Movie}.
     *
     * @param id The ID of the movie
     *
     * @return the movie
     */
    @RequestMapping("/{id}/original")
    public Movie getOriginalById(@PathVariable("id") final long id) {
        checkPermission(getUser(), Permission.MOVIE_MOD);
        return findOne(movieRepository, id, "movie");
    }

    /**
     * Get a list of short versions of all movies.
     *
     * @param includeGenres    Should include genres
     * @param includeLanguages Should include languages
     * @param includeAgeRating Should include age rating
     *
     * @return the list
     */
    @RequestMapping
    public List<MovieSummary> summaries(
            @RequestParam(required = false, defaultValue = "false") final boolean useDutchTitles,
            @RequestParam(required = false, defaultValue = "false") final boolean useOriginalTitles,
            @RequestParam(required = false, defaultValue = "false") final boolean includeGenres,
            @RequestParam(required = false, defaultValue = "false") final boolean includeLanguages,
            @RequestParam(required = false, defaultValue = "false") final boolean includeAgeRating) {
        return movieRepository.findAllByOrderByTitleAsc()
                .stream()
                .map(m -> MovieSummary.convert(m, useDutchTitles, useOriginalTitles,
                        includeGenres, includeLanguages, includeAgeRating))
                .collect(Collectors.toList());
    }

    /**
     * Add a new Movie to the DB.
     *
     * @param postedRequest The data
     */
    @Secured(Permission.MOVIE_MOD)
    @Transactional
    @RequestMapping(method = RequestMethod.POST)
    public void addMovie(@Valid @RequestBody PostedMovieRequest postedRequest) {
        //Validate
        JwtUser user = getUser();
        movieAddBean.checkIfMovieNotAdded(postedRequest.getImdbId());
        movieAddBean.getMovieData(postedRequest.getImdbId());

        //Add the movie
        movieAddBean.addMovie(postedRequest.getImdbId(), postedRequest.getYoutubeId(), new User(user.getId()));
    }

}
