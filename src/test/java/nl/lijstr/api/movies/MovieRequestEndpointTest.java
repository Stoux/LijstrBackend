package nl.lijstr.api.movies;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import nl.lijstr.api.movies.models.MovieShortRequest;
import nl.lijstr.api.movies.models.post.MovieRatingRequest;
import nl.lijstr.api.movies.models.post.PostedMovieRatingRequest;
import nl.lijstr.beans.MovieAddBean;
import nl.lijstr.beans.UserBean;
import nl.lijstr.common.Container;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.movies.MovieRating;
import nl.lijstr.domain.movies.MovieRequest;
import nl.lijstr.domain.users.User;
import nl.lijstr.exceptions.BadRequestException;
import nl.lijstr.repositories.movies.MovieRatingRepository;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.repositories.movies.MovieRequestRepository;
import nl.lijstr.services.omdb.models.OmdbObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static nl.lijstr._TestUtils.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Stoux on 6-2-2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class MovieRequestEndpointTest {

    public static final String IMDB_ID = "imdbId";
    public static final String TITLE = "title";
    public static final String YOUTUBE_ID = "youtubeId";
    public static final String COMMENT = "comment";
    @Mock
    private UserBean userBean;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private MovieRequestRepository requestRepository;
    @Mock
    private MovieRatingRepository ratingRepository;
    @Mock
    private MovieAddBean movieAddBean;

    private MovieRequestEndpoint endpoint;

    @Before
    public void setUp() throws Exception {
        endpoint = new MovieRequestEndpoint();
        insertMocks(endpoint, userBean, movieRepository, requestRepository, ratingRepository, movieAddBean);
        when(userBean.getJwtUser()).thenReturn(createUser(1L));
    }

    @Test
    public void list() throws Exception {
        //Arrange
        List<MovieRequest> ratings = Arrays.asList(createRequest(1L), createRequest(2L));
        when(requestRepository.findAll()).thenReturn(ratings);

        //Act
        List<MovieShortRequest> list = endpoint.list();

        //Assert
        assertEquals(2, list.size());
        assertEquals(1L, list.get(0).getId());
        assertEquals(2L, list.get(1).getId());
    }

    @Test
    public void requestMovie() throws Exception {
        //Arrange
        OmdbObject omdbObject = new OmdbObject("title", "year", "rating", "movie");
        when(movieAddBean.getMovieData(eq(IMDB_ID))).thenReturn(omdbObject);

        Container<MovieRequest> container = new Container<>();
        when(requestRepository.save(any(MovieRequest.class)))
                .thenAnswer(i -> storeInvoked(i, container));

        MovieRatingRequest rating = new MovieRatingRequest(MovieRating.Seen.YES, BigDecimal.TEN, COMMENT);
        PostedMovieRatingRequest request = new PostedMovieRatingRequest(IMDB_ID, YOUTUBE_ID, rating);

        //Act
        endpoint.requestMovie(request);

        //Assert
        verify(movieAddBean, times(1)).checkIfMovieNotAdded(eq(IMDB_ID));
        verify(movieAddBean, times(1)).getMovieData(eq(IMDB_ID));
        verify(requestRepository, times(1)).save(any(MovieRequest.class));

        assertTrue(container.isPresent());
        MovieRequest item = container.getItem();
        assertNotNull(item);

        assertEquals(1L, item.getUser().getId().longValue());
        assertEquals(IMDB_ID, item.getImdbId());
        assertEquals(YOUTUBE_ID, item.getYoutubeUrl());
        assertEquals(omdbObject.getTitle(), item.getTitle());
        assertEquals(omdbObject.getYear(), item.getYear());
        assertEquals(omdbObject.getImdbRating(), item.getImdbRating());

        assertEquals(MovieRating.Seen.YES, item.getSeen());
        assertEquals(BigDecimal.TEN, item.getRating());
        assertEquals(COMMENT, item.getComment());
    }

    @Test
    public void approveRequest() throws Exception {
        //Arrange
        MovieRequest request = createRequestWithRating(2L);

        when(requestRepository.findOne(eq(2L))).thenReturn(request);
        Movie movie = new Movie(IMDB_ID);
        when(movieAddBean.addMovie(eq(IMDB_ID), eq(TITLE), eq(YOUTUBE_ID), eq(request.getUser())))
                .thenReturn(movie);

        Container<MovieRating> container = new Container<>();
        when(ratingRepository.saveAndFlush(any(MovieRating.class)))
                .thenAnswer(i -> storeInvoked(i, container));

        //Act
        endpoint.approveRequest(2L);

        //Assert
        verify(movieAddBean, times(1)).checkIfMovieNotAdded(eq(IMDB_ID));
        verify(requestRepository, times(1)).save(eq(request));

        assertEquals(1L, request.getApprovedBy().getId().longValue());

        assertTrue(container.isPresent());
        MovieRating item = container.getItem();
        assertEquals(movie, item.getMovie());
        assertEquals(request.getUser(), item.getUser());
        assertEquals(MovieRating.Seen.YES, item.getSeen());
        assertEquals(BigDecimal.ONE, item.getRating());
        assertEquals(COMMENT, item.getComment());
    }

    @Test
    public void approveAlreadyAddedRequest() throws Exception {
        //Arrange
        MovieRequest request = createRequestWithRating(2L);
        when(requestRepository.findOne(eq(2L))).thenReturn(request);
        doThrow(new BadRequestException("Already added!")).when(movieAddBean).checkIfMovieNotAdded(eq(IMDB_ID));

        Movie m = new Movie();
        when(movieRepository.findByImdbId(IMDB_ID)).thenReturn(m);

        Container<MovieRating> container = new Container<>();
        when(ratingRepository.saveAndFlush(any(MovieRating.class)))
                .thenAnswer(i -> storeInvoked(i, container));

        //Act
        endpoint.approveRequest(2L);

        //Assert
        verify(requestRepository, times(1)).delete(eq(request));
        assertContainerContent(container, m, request);
    }

    private void assertContainerContent(Container<MovieRating> container, Movie movie, MovieRequest request) {
        assertTrue(container.isPresent());
        MovieRating item = container.getItem();
        assertEquals(movie, item.getMovie());
        assertEquals(request.getUser(), item.getUser());
        assertEquals(MovieRating.Seen.YES, item.getSeen());
        assertEquals(BigDecimal.ONE, item.getRating());
        assertEquals(COMMENT, item.getComment());
    }


    private MovieRequest createRequest(long id) {
        MovieRequest r = new MovieRequest();
        r.setId(id);
        r.setUser(new User(id));
        r.setImdbId(IMDB_ID);
        r.setTitle(TITLE);
        r.setYoutubeUrl(YOUTUBE_ID);
        return r;
    }

    private MovieRequest createRequestWithRating(long id) {
        MovieRequest request = createRequest(id);
        request.setSeen(MovieRating.Seen.YES);
        request.setRating(BigDecimal.ONE);
        request.setComment(COMMENT);
        return request;
    }

}