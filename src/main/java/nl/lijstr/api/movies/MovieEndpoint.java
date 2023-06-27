package nl.lijstr.api.movies;

import nl.lijstr.api.abs.AbsService;
import nl.lijstr.api.movies.models.MovieDetail;
import nl.lijstr.api.movies.models.MovieSummary;
import nl.lijstr.api.movies.models.post.PostedMovieRequest;
import nl.lijstr.beans.MovieAddBean;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.domain.users.User;
import nl.lijstr.exceptions.BadRequestException;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.security.model.JwtUser;
import nl.lijstr.services.omdb.models.OmdbObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
            @RequestParam(required = false, defaultValue = "true") final boolean includeCollections,
            @RequestParam(required = false, name = "users") final String requestedUsers) {
        Set<Long> users = parseUsers(requestedUsers);
        return movieRepository.findAllByOrderByTitleAsc()
                .stream()
                .map(m -> MovieSummary.convert(m, useDutchTitles, useOriginalTitles,
                        includeGenres, includeLanguages, includeAgeRating, includeCollections, users))
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
        OmdbObject movieData = movieAddBean.getMovieData(postedRequest.getImdbId());

        //Add the movie
        movieAddBean.addMovie(
            postedRequest.getImdbId(), movieData.getTitle(), postedRequest.getYoutubeId(), new User(user.getId())
        );
    }

    @SuppressWarnings("squid:S1168")
    private static Set<Long> parseUsers(final String requestedUsers) {
        if (requestedUsers == null) {
            //Explicitly return null instead of an empty array as an empty array means return everything available
            return null;
        }

        if (requestedUsers.length() == 0) {
            return Collections.emptySet();
        }

        if (!requestedUsers.matches("^(\\d+)(,\\d+)*$")) {
            throw new BadRequestException("Invalid user list");
        }

        String[] split = requestedUsers.split(",");
        return Arrays.stream(split)
            .map(Long::parseLong)
            .collect(Collectors.toSet());
    }

}
