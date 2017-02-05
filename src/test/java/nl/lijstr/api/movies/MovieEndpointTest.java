package nl.lijstr.api.movies;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.lijstr.api.movies.models.MovieSummary;
import nl.lijstr.api.movies.models.post.PostedMovieRequest;
import nl.lijstr.beans.UserBean;
import nl.lijstr.common.Container;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.movies.MovieRequest;
import nl.lijstr.exceptions.BadRequestException;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.repositories.movies.MovieRequestRepository;
import nl.lijstr.security.model.JwtUser;
import nl.lijstr.services.maf.MafApiService;
import nl.lijstr.services.omdb.OmdbApiService;
import nl.lijstr.services.omdb.models.OmdbObject;
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
public class MovieEndpointTest {

    @Mock
    private UserBean userBean;
    @Mock
    private MafApiService apiService;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private MovieRequestRepository movieRequestRepository;
    @Mock
    private OmdbApiService omdbApiService;

    private MovieEndpoint movieEndpoint;

    @Before
    public void setUp() throws Exception {
        movieEndpoint = new MovieEndpoint();
        insertMocks(movieEndpoint, apiService, movieRepository, movieRequestRepository, omdbApiService, userBean);

        LocalDateTime access = LocalDateTime.now().plusMinutes(5);
        when(userBean.getJwtUser())
                .thenReturn(new JwtUser(1L, "User", "Pass", new ArrayList<>(), access, access, 1L));
    }

    @Test
    public void summaries() throws Exception {
        //Arrange
        Movie movie1 = createEmptyMovie(1);
        Movie movie2 = createEmptyMovie(2);
        Movie movie3 = createEmptyMovie(3);
        when(movieRepository.findAllByOrderByTitleAsc()).thenReturn(Arrays.asList(movie1, movie2, movie3));

        //Act
        List<MovieSummary> summaries = movieEndpoint.summaries(false, false,
                false, false, false);

        //Assert
        assertEquals(3, summaries.size());
        assertEquals(1L, summaries.get(0).getId());
        assertEquals(2L, summaries.get(1).getId());
        assertEquals(3L, summaries.get(2).getId());
    }

    @Test
    public void requestMovie() throws Exception {
        //Arrange
        final Container<MovieRequest> catchContainer = new Container<>();
        OmdbObject omdbObject = new OmdbObject("Title", "Year", "Rating", "movie");

        when(movieRepository.findByImdbId(anyString())).thenReturn(null);
        when(omdbApiService.getMovie(anyString())).thenReturn(omdbObject);
        when(movieRequestRepository.save(any(MovieRequest.class))).thenAnswer(invocation -> {
            MovieRequest movieRequest = getInvocationParam(invocation, 0);
            catchContainer.setItem(movieRequest);
            return movieRequest;
        });

        //Act
        movieEndpoint.requestMovie(new PostedMovieRequest("imdbId", "youtubeId"));

        //Arrange
        assertTrue(catchContainer.isPresent());
        MovieRequest movieRequest = catchContainer.getItem();
        assertEquals(omdbObject.getTitle(), movieRequest.getTitle());
        assertEquals(omdbObject.getImdbRating(), movieRequest.getImdbRating());
        assertEquals(omdbObject.getYear(), movieRequest.getYear());
        assertEquals("imdbId", movieRequest.getImdbId());
        assertEquals(Long.valueOf(1L), movieRequest.getUser().getId());
    }

    @Test(expected = BadRequestException.class)
    public void requestExistingMovie() throws Exception {
        //Arrange
        when(movieRepository.findByImdbId(anyString())).thenReturn(new Movie(""));

        //Act
        movieEndpoint.requestMovie(new PostedMovieRequest("", ""));

        //Assert
        fail("The movie already exists");
    }

    @Test
    public void addMovie() throws Exception {
        //Arrange
        PostedMovieRequest postedRequest = new PostedMovieRequest("imdbId", "youtubeId");
        final Container<Movie> catchContainer = new Container<>();
        OmdbObject omdbObject = new OmdbObject("Title", "Year", "Rating", "movie");

        when(movieRepository.findByImdbId(anyString())).thenReturn(null);
        when(omdbApiService.getMovie(anyString())).thenReturn(omdbObject);
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> getInvocationParam(invocation, 0));
        when(apiService.updateMovie(any())).thenAnswer(invocation -> {
            Movie movie = getInvocationParam(invocation, 0);
            catchContainer.setItem(movie);
            return movie;
        });

        //Act
        movieEndpoint.addMovie(postedRequest);

        //Assert
        assertTrue(catchContainer.isPresent());
        Movie movie = catchContainer.getItem();
        assertEquals(postedRequest.getImdbId(), movie.getImdbId());
        assertEquals(postedRequest.getYoutubeId(), movie.getYoutubeUrl());
        assertEquals(Long.valueOf(1L), movie.getAddedBy().getId());
    }

    private Movie createEmptyMovie(int id) {
        Movie movie = new Movie("i" + id);
        movie.setId((long) id);
        movie.setTitle("t" + id);
        movie.setYear(id);
        movie.setLatestMovieRatings(new ArrayList<>());
        return movie;
    }

}