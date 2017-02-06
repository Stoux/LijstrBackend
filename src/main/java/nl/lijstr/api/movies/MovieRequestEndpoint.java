package nl.lijstr.api.movies;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import nl.lijstr.api.abs.AbsService;
import nl.lijstr.api.movies.models.MovieShortRequest;
import nl.lijstr.api.movies.models.post.MovieRatingRequest;
import nl.lijstr.api.movies.models.post.PostedMovieRatingRequest;
import nl.lijstr.beans.MovieAddBean;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.movies.MovieRating;
import nl.lijstr.domain.movies.MovieRequest;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.domain.users.User;
import nl.lijstr.exceptions.BadRequestException;
import nl.lijstr.repositories.movies.MovieRatingRepository;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.repositories.movies.MovieRequestRepository;
import nl.lijstr.security.model.JwtUser;
import nl.lijstr.services.omdb.models.OmdbObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoint for requesting new movies.
 */
@Secured(Permission.MOVIE_USER)
@RestController
@RequestMapping(value = "/movies/request", produces = "application/json")
public class MovieRequestEndpoint extends AbsService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieRequestRepository requestRepository;

    @Autowired
    private MovieRatingRepository ratingRepository;

    @Autowired
    private MovieAddBean movieAddBean;

    /**
     * Fetch a list of all open requests.
     *
     * @return the open requests
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<MovieShortRequest> list() {
        return requestRepository.findAll().stream()
                .map(MovieShortRequest::new)
                .collect(Collectors.toList());
    }

    /**
     * Request a Movie.
     *
     * @param postedRequest The data
     */
    @Transactional
    @RequestMapping(method = RequestMethod.POST)
    public void requestMovie(@Valid @RequestBody PostedMovieRatingRequest postedRequest) {
        JwtUser user = getUser();
        movieAddBean.checkIfMovieNotAdded(postedRequest.getImdbId());
        OmdbObject omdb = movieAddBean.getMovieData(postedRequest.getImdbId());

        MovieRatingRequest ratingRequest = postedRequest.getRatingRequest();
        MovieRequest request = new MovieRequest(
                new User(user.getId()), postedRequest.getImdbId(), postedRequest.getYoutubeId(),
                omdb.getTitle(), omdb.getYear(), omdb.getImdbRating(),
                ratingRequest.getSeen(), ratingRequest.getRating(), ratingRequest.getComment()
        );
        requestRepository.save(request);
    }

    /**
     * Approve a movie request.
     *
     * @param requestId The ID of the request
     */
    @Secured(Permission.MOVIE_MOD)
    @RequestMapping(value = "/{requestId:\\d+}/approve", method = RequestMethod.POST)
    public void approveRequest(@PathVariable() Long requestId) {
        JwtUser user = getUser();
        MovieRequest request = findOne(requestRepository, requestId, "Request");

        //Add the movie
        Movie movie;
        try {
            //Add the movie and finish the request
            movieAddBean.checkIfMovieNotAdded(request.getImdbId());
            movie = movieAddBean.addMovie(request.getImdbId(), request.getYoutubeUrl(), request.getUser());
            request.setApprovedBy(new User(user.getId()));
            requestRepository.save(request);
        } catch (BadRequestException e) {
            //Somehow already added, delete the request
            requestRepository.delete(request);
            movie = movieRepository.findByImdbId(request.getImdbId());
            checkIfFound(movie);
        }

        //Add the rating
        ratingRepository.saveAndFlush(new MovieRating(
                movie, request.getUser(),
                request.getSeen(), request.getRating(), request.getComment()
        ));
    }

}
