package nl.lijstr.api.movies;

import java.util.Map;
import java.util.Optional;
import javax.validation.Valid;
import nl.lijstr.api.abs.AbsMovieService;
import nl.lijstr.api.movies.models.post.PostedMovieComment;
import nl.lijstr.common.Utils;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.movies.MovieComment;
import nl.lijstr.domain.other.FieldHistory;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.domain.users.User;
import nl.lijstr.exceptions.db.NotFoundException;
import nl.lijstr.exceptions.security.UnauthorizedException;
import nl.lijstr.repositories.movies.MovieCommentRepository;
import nl.lijstr.repositories.other.FieldHistoryRepository;
import nl.lijstr.security.model.JwtUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Stoux on 17/04/2016.
 */
@Secured(Permission.MOVIE_USER)
@RestController
@RequestMapping(value = "/movies/{movieId:\\d+}/comments", produces = "application/json")
public class MovieCommentEndpoint extends AbsMovieService {

    @Autowired
    private FieldHistoryRepository historyRepository;

    @Autowired
    private MovieCommentRepository commentRepository;

    /**
     * Add a new comment.
     *
     * @param movieId       The ID of the movie
     * @param postedComment The new comment
     *
     * @return map with comment info
     */
    @RequestMapping(method = RequestMethod.POST)
    public Map add(@PathVariable() Long movieId,
                   @Valid @RequestBody PostedMovieComment postedComment) {
        JwtUser user = getUser();
        Movie movie = findMovie(movieId);
        MovieComment movieComment = new MovieComment(movie, new User(user.getId()), postedComment.getComment());
        MovieComment updatedComment = commentRepository.saveAndFlush(movieComment);
        return commentMap(updatedComment);
    }

    /**
     * Update a comment.
     *
     * @param movieId       The ID of the movie
     * @param commentId     The ID of the comment
     * @param postedComment The updated comment
     *
     * @return map with comment info
     */
    @Transactional
    @RequestMapping(value = "/{commentId:\\d+}", method = RequestMethod.PUT)
    public Map edit(@PathVariable() Long movieId,
                    @PathVariable() long commentId,
                    @Valid @RequestBody PostedMovieComment postedComment) {
        //Find the comment
        JwtUser user = getUser();
        Movie movie = findMovie(movieId);
        Optional<MovieComment> optComment = movie.getMovieComments().stream()
                .filter(movieComment -> movieComment.getId().equals(commentId))
                .findFirst();

        if (!optComment.isPresent()) {
            throw new NotFoundException();
        }
        MovieComment comment = optComment.get();

        //Check if the user has access
        if (!user.getId().equals(comment.getUser().getId())) {
            throw new UnauthorizedException();
        }

        //Update the comment
        String oldComment = comment.getComment();
        comment.setComment(postedComment.getComment());
        MovieComment updatedComment = commentRepository.saveAndFlush(comment);

        //=> Keep history
        FieldHistory history = new FieldHistory(
                FieldHistory.getDatabaseClassName(MovieComment.class),
                commentId, "comment", oldComment, updatedComment.getComment()
        );
        historyRepository.saveAndFlush(history);

        return commentMap(updatedComment);
    }

    private Map commentMap(MovieComment comment) {
        return Utils.asMap(
                "id", comment.getId(),
                "comment", comment.getComment()
        );
    }

}
