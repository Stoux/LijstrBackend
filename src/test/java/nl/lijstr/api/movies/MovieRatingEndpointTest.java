package nl.lijstr.api.movies;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.lijstr.api.movies.models.MovieShortRating;
import nl.lijstr.api.movies.models.post.MovieRatingRequest;
import nl.lijstr.beans.UserBean;
import nl.lijstr.common.Container;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.movies.MovieRating;
import nl.lijstr.domain.users.User;
import nl.lijstr.exceptions.db.ConflictException;
import nl.lijstr.repositories.movies.MovieRatingRepository;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.security.model.JwtUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static nl.lijstr._TestUtils.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Stoux on 24/04/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class MovieRatingEndpointTest {

    @Mock
    private UserBean userBean;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private MovieRatingRepository movieRatingRepository;

    private MovieRatingEndpoint endpoint;

    @Before
    public void setUp() throws Exception {
        endpoint = new MovieRatingEndpoint();
        insertMocks(endpoint, userBean, movieRepository, movieRatingRepository);
    }

    @Test
    public void add() throws Exception {
        //Arrange
        Movie movie = new Movie();
        movie.setLatestMovieRatings(new ArrayList<>());
        movie.setId(1L);
        JwtUser jwtUser = createUser(1L);
        MovieRatingRequest newRating = new MovieRatingRequest(null, new BigDecimal("9.1"), null);

        when(userBean.getJwtUser()).thenReturn(jwtUser);
        when(movieRepository.findOne(anyLong())).thenReturn(movie);
        when(movieRatingRepository.saveAndFlush(any())).thenAnswer(invocation -> getInvocationParam(invocation, 0));

        //Act
        MovieShortRating added = endpoint.add(1L, newRating);

        //Assert
        assertEquals(newRating.getRating(), added.getRating());
        assertEquals(MovieRating.Seen.UNKNOWN.ordinal(), added.getSeen());
        assertNull(added.getComment());
        assertEquals(1L, added.getUser());
    }

    @Test
    public void addRating() throws Exception {
        //Arrange
        JwtUser user = createUser(1L);
        Movie movie = new Movie();
        movie.setLatestMovieRatings(new ArrayList<>());
        MovieRatingRequest request = new MovieRatingRequest(true, new BigDecimal("9.1"), null);

        when(movieRatingRepository.saveAndFlush(any())).thenAnswer(invocation -> getInvocationParam(invocation, 0));

        //Act
        MovieRating rating = ReflectionTestUtils.invokeMethod(endpoint, "addRating", user, movie, request);

        //Assert
        assertEquals(user.getId(), rating.getUser().getId());
        assertEquals(request.getRating(), rating.getRating());
        assertNull(request.getComment());
        assertEquals(MovieRating.Seen.YES, rating.getSeen());
        verify(movieRatingRepository, times(1)).saveAndFlush(eq(rating));
    }

    @Test
    public void addRatingWithExisting() throws Exception {
        //Arrange
        JwtUser user = createUser(1L);
        Movie movie = new Movie();
        movie.setId(1L);
        MovieRating ownRating = createRating(1, LocalDateTime.now().minusHours(2));
        movie.setLatestMovieRatings(Arrays.asList(
                createRating(2, LocalDateTime.now().minusHours(1)), ownRating
        ));
        MovieRatingRequest request = new MovieRatingRequest(true, new BigDecimal("9.1"), null);

        Container<MovieRating> container = new Container<>();
        when(movieRatingRepository.save(any(MovieRating.class))).thenAnswer(invocation -> {
            container.setItem(getInvocationParam(invocation, 0));
            return container.getItem();
        });
        when(movieRatingRepository.saveAndFlush(any())).thenAnswer(invocation -> getInvocationParam(invocation, 0));

        //Act
        MovieRating rating = ReflectionTestUtils.invokeMethod(endpoint, "addRating", user, movie, request);

        //Assert
        assertTrue(container.isPresent());
        assertFalse(ownRating.getLatest());
        assertTrue(rating.getLatest());
        verify(movieRatingRepository, times(1)).save(eq(ownRating));
    }

    @Test(expected = ConflictException.class)
    public void addRatingWithRecentExisting() throws Exception {
        //Arrange
        JwtUser user = createUser(1L);
        Movie movie = new Movie();
        movie.setLatestMovieRatings(Arrays.asList(
                createRating(1L, LocalDateTime.now())
        ));

        //Act
        ReflectionTestUtils.invokeMethod(endpoint, "addRating", user, movie, null);

        //Assert
        fail("Should warn about the old rating");
    }

    private MovieRating createRating(long userId, LocalDateTime validTill) {
        MovieRating rating = new MovieRating();
        rating.setId(userId);
        rating.setUser(new User(userId));
        rating.setLatest(true);
        rating.setCreated(validTill);
        return rating;
    }

    private JwtUser createUser(long id) {
        JwtUser user = new JwtUser();
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }


    @Test
    public void edit() throws Exception {
        //Arrange
        JwtUser user = createUser(1L);
        Movie movie = new Movie();
        MovieRating oldRating = createRating(user.getId(), LocalDateTime.now());
        oldRating.setRating(new BigDecimal("9.1"));
        oldRating.setComment("Test");
        movie.setLatestMovieRatings(Arrays.asList(
                oldRating
        ));

        when(userBean.getJwtUser()).thenReturn(user);
        when(movieRepository.findOne(anyLong())).thenReturn(movie);
        when(movieRatingRepository.saveAndFlush(any()))
                .thenAnswer(invocation -> getInvocationParam(invocation, 0));

        //Act
        MovieShortRating rating = endpoint.edit(1L, 1L, new MovieRatingRequest(true, new BigDecimal("9.1"), null));

        //Assert
        assertEquals(MovieRating.Seen.YES.ordinal(), rating.getSeen());
        assertEquals(oldRating.getRating(), rating.getRating());
        assertEquals(oldRating.getComment(), rating.getComment());
        verify(movieRatingRepository, times(1)).saveAndFlush(eq(oldRating));
    }

    @Test(expected = ConflictException.class)
    public void editNonExistent() throws Exception {
        failedEdit(new ArrayList<>(), "No rating");
    }

    @Test(expected = ConflictException.class)
    public void editWrongRating() throws Exception {
        MovieRating rating = createRating(1L, LocalDateTime.now());
        rating.setId(2L);
        failedEdit(Arrays.asList(rating), "Wrong rating");
    }

    @Test(expected = ConflictException.class)
    public void editNotRecent() throws Exception {
        MovieRating rating = createRating(1L, LocalDateTime.now().minusHours(24));
        failedEdit(Arrays.asList(rating), "Not a recent rating");
    }


    private void failedEdit(List<MovieRating> latestRatings, String failMessage) {
        //Arrange
        JwtUser user = createUser(1L);
        Movie movie = new Movie();
        movie.setLatestMovieRatings(latestRatings);

        when(userBean.getJwtUser()).thenReturn(user);
        when(movieRepository.findOne(anyLong())).thenReturn(movie);

        //Act
        endpoint.edit(1L, 1L, null);

        //Assert
        fail(failMessage);
    }


}