package nl.lijstr.api.movies;

import java.util.List;
import javax.validation.Valid;
import nl.lijstr.api.abs.base.TargetEndpoint;
import nl.lijstr.api.abs.base.model.post.PostedRequest;
import nl.lijstr.api.movies.models.MovieDetail;
import nl.lijstr.api.movies.models.MovieSummary;
import nl.lijstr.beans.MovieAddBean;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.domain.users.User;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.security.model.JwtUser;
import nl.lijstr.services.omdb.models.OmdbObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * The Movies Endpoint.
 */
@RestController
@RequestMapping(value = "/movies", produces = "application/json")
public class MovieEndpoint extends TargetEndpoint<Movie, MovieRepository> {

    private final MovieAddBean movieAddBean;

    @Autowired
    public MovieEndpoint(MovieRepository targetRepository, MovieAddBean movieAddBean) {
        super(targetRepository, "movie");
        this.movieAddBean = movieAddBean;
    }

    /**
     * Get a {@link Movie} as detail view.
     *
     * @param id The ID of the movie
     *
     * @return the movie detail
     */
    @RequestMapping(DETAIL_PATH)
    public MovieDetail getById(@PathVariable("id") final long id) {
        Movie movie = findOne(targetRepository, id, "movie");
        return MovieDetail.fromMovie(movie);
    }

    /**
     * Get the original {@link Movie}.
     *
     * @param id The ID of the movie
     *
     * @return the movie
     */
    @Secured(Permission.MOVIE_MOD)
    @RequestMapping(ORIGINAL_PATH)
    public Movie getOriginalById(@PathVariable("id") final long id) {
        return findOne(targetRepository, id, "movie");
    }

    /**
     * Get a list of summaries of all movies.
     *
     * @param useDutchTitles    Use the dutch titles if available
     * @param useOriginalTitles Use the original titles if available
     * @param includeGenres     Should include genres
     * @param includeLanguages  Should include languages
     * @param includeAgeRating  Should include age rating
     * @param requestedUsers    A comma separated list of all the requested users which will only return their ratings
     *
     * @return the list
     */
    @RequestMapping
    public List<MovieSummary> summaries(
            @RequestParam(required = false, defaultValue = "false") final boolean useDutchTitles,
            @RequestParam(required = false, defaultValue = "false") final boolean useOriginalTitles,
            @RequestParam(required = false, defaultValue = "false") final boolean includeGenres,
            @RequestParam(required = false, defaultValue = "false") final boolean includeLanguages,
            @RequestParam(required = false, defaultValue = "false") final boolean includeAgeRating,
            @RequestParam(required = false, name = "users") final String requestedUsers) {
        return summaryList(requestedUsers,
                           (m, users) -> MovieSummary.convert(m, useDutchTitles, useOriginalTitles, includeGenres,
                                                              includeLanguages, includeAgeRating, users));
    }

    /**
     * Add a new Movie to the DB.
     *
     * @param postedRequest The data
     */
    @Secured(Permission.MOVIE_MOD)
    @Transactional
    @RequestMapping(method = RequestMethod.POST)
    public void addMovie(@Valid @RequestBody PostedRequest postedRequest) {
        //Validate
        JwtUser user = getUser();
        movieAddBean.checkIfMovieNotAdded(postedRequest.getImdbId());
        OmdbObject movieData = movieAddBean.getMovieData(postedRequest.getImdbId());

        //Add the movie
        movieAddBean.addMovie(postedRequest.getImdbId(), movieData.getTitle(), postedRequest.getYoutubeId(),
                              new User(user.getId()));
    }

}
