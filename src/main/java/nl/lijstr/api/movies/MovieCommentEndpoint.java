package nl.lijstr.api.movies;

import nl.lijstr.api.abs.AbsService;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.repositories.movies.MovieCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Stoux on 17/04/2016.
 */
@Secured(Permission.MOVIE)
@RestController
@RequestMapping(value = "/movies/{movieId:\\d+}/comments", produces = "application/json")
public class MovieCommentEndpoint extends AbsService {

    @Autowired
    private MovieCommentRepository repository;

    /**
     * Add a new comment.
     *
     * @param movieId The ID of the movie
     * @param comment The new comment
     */
    @RequestMapping(method = RequestMethod.POST)
    public void add(@PathVariable() long movieId, @RequestParam() String comment) {
        //TODO: Get user
        //TODO: Add comment
    }

    /**
     * Update a comment.
     *
     * @param movieId   The ID of the movie
     * @param commentId The ID of the comment
     * @param comment   The updated comment
     */
    @RequestMapping(value = "/{commentId:\\d+}", method = RequestMethod.PUT)
    public void update(@PathVariable() long movieId, @PathVariable() long commentId, @RequestParam() String comment) {
        //TODO: Get user
        //TODO: Find comment
        //TODO: Update it
    }

}
