package nl.lijstr.api.movies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import nl.lijstr.api.movies.models.post.PostedMovieComment;
import nl.lijstr.beans.UserBean;
import nl.lijstr.common.Container;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.movies.MovieComment;
import nl.lijstr.domain.other.FieldHistory;
import nl.lijstr.domain.users.User;
import nl.lijstr.exceptions.db.NotFoundException;
import nl.lijstr.exceptions.security.UnauthorizedException;
import nl.lijstr.repositories.movies.MovieCommentRepository;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.repositories.other.FieldHistoryRepository;
import nl.lijstr.security.model.JwtUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static nl.lijstr._TestUtils.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Stoux on 24/04/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class MovieCommentEndpointTest {

    @Mock
    private UserBean userBean;
    @Mock
    private FieldHistoryRepository historyRepository;
    @Mock
    private MovieCommentRepository commentRepository;
    @Mock
    private MovieRepository movieRepository;

    private MovieCommentEndpoint endpoint;

    @Before
    public void setUp() throws Exception {
        endpoint = new MovieCommentEndpoint();
        insertMocks(endpoint, userBean, movieRepository, historyRepository, commentRepository);
    }

    @Test
    public void add() throws Exception {
        //Arrange
        JwtUser user = createUser(1L);
        Movie movie = new Movie();
        String comment = "Test comment";
        Container<MovieComment> catchContainer = new Container<>();

        when(userBean.getJwtUser()).thenReturn(user);
        when(movieRepository.findOne(anyLong())).thenReturn(movie);
        when(commentRepository.saveAndFlush(any())).thenAnswer(invocation -> {
            MovieComment movieComment = getInvocationParam(invocation, 0);
            movieComment.setId(1L);
            catchContainer.setItem(movieComment);
            return movieComment;
        });

        //Act
        Map commentMap = endpoint.add(1L, new PostedMovieComment(comment));

        //Assert
        assertTrue(catchContainer.isPresent());
        MovieComment movieComment = catchContainer.getItem();
        assertEquals(1L, commentMap.get("id"));
        assertEquals(Long.valueOf(1L), movieComment.getId());
        assertEquals(comment, commentMap.get("comment"));
        assertEquals(movie, movieComment.getMovie());
        assertEquals(user.getId(), movieComment.getUser().getId());
    }

    @Test
    public void edit() throws Exception {
        //Arrange
        long commentId = 1L;
        long movieId = 10L;

        String newComment = "New comment";
        String oldCommentText = "Old comment";
        JwtUser user = createUser(1L);
        Movie movie = new Movie();

        MovieComment oldComment = createComment(user.getId(), commentId);
        oldComment.setComment(oldCommentText);
        movie.setMovieComments(Arrays.asList(oldComment));

        Container<MovieComment> commentContainer = new Container<>();
        Container<FieldHistory> historyContainer = new Container<>();

        when(userBean.getJwtUser()).thenReturn(user);
        when(movieRepository.findOne(eq(movieId))).thenReturn(movie);
        when(commentRepository.saveAndFlush(any())).thenAnswer(invocation -> {
            commentContainer.setItem(getInvocationParam(invocation, 0));
            return commentContainer.getItem();
        });
        when(historyRepository.saveAndFlush(any())).thenAnswer(invocation -> {
            historyContainer.setItem(getInvocationParam(invocation, 0));
            return historyContainer.getItem();
        });

        //Act
        Map commentMap = endpoint.edit(movieId, commentId, new PostedMovieComment(newComment));

        //Assert
        assertTrue(commentContainer.isPresent());
        assertTrue(historyContainer.isPresent());

        MovieComment updatedComment = commentContainer.getItem();
        FieldHistory history = historyContainer.getItem();

        assertEquals(oldComment, updatedComment);
        assertEquals(updatedComment.getComment(), newComment);

        assertEquals(oldCommentText, history.getOldValue());
        assertEquals(newComment, history.getNewValue());
        assertEquals(Long.valueOf(commentId), history.getObjectId());
        assertEquals("comment", history.getField());

        assertEquals(commentId, commentMap.get("id"));
        assertEquals(newComment, commentMap.get("comment"));
    }

    @Test(expected = NotFoundException.class)
    public void editNotFound() throws Exception {
        //Arrange
        JwtUser user = createUser(1L);
        Movie movie = new Movie();
        movie.setMovieComments(new ArrayList<>());

        when(userBean.getJwtUser()).thenReturn(user);
        when(movieRepository.findOne(anyLong())).thenReturn(movie);

        //Act
        endpoint.edit(1L, 1L, null);

        //Fail
        fail("Movie doesn't exist");
    }

    @Test(expected = UnauthorizedException.class)
    public void editWrongUser() throws Exception {
        //Arrange
        long commentId = 1L;
        JwtUser user = createUser(1L);
        Movie movie = new Movie();
        MovieComment comment = createComment(2L, commentId);
        movie.setMovieComments(Arrays.asList(comment));

        when(userBean.getJwtUser()).thenReturn(user);
        when(movieRepository.findOne(anyLong())).thenReturn(movie);

        //Act
        endpoint.edit(1L, commentId, null);

        //Fail
        fail("Comment belongs to a different user");
    }

    private MovieComment createComment(long userId, long commentId) {
        MovieComment movieComment = new MovieComment(null, new User(userId), String.valueOf(commentId));
        movieComment.setId(commentId);
        return movieComment;
    }


}