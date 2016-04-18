package nl.lijstr.api.movies;

import nl.lijstr.api.abs.AbsService;
import nl.lijstr.domain.movies.MovieRating;
import nl.lijstr.repositories.movies.MovieRatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Stoux on 17/04/2016.
 */
@RestController
@RequestMapping(value = "/movies/{movieId:\\d+}/ratings", produces = "application/json")
public class MovieRatingEndpoint extends AbsService {

    @Autowired
    private MovieRatingRepository repository;

    /**
     * Add a new comment
     *
     * @param movieId     The ID of the movie
     * @param movieRating The new Rating
     */
    @RequestMapping(method = RequestMethod.POST)
    public void add(@PathVariable() long movieId, @RequestBody() MovieRating movieRating) {
        //TODO: Get user
        //TODO: Add rating
    }

}
