package nl.lijstr.api.movies;

import nl.lijstr.common.Container;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.services.maf.MafApiService;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.*;
import static nl.lijstr._TestUtils.TestUtils.*;
import static org.mockito.Mockito.*;

/**
 * Created by Stoux on 24/04/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class MovieUpdateEndpointTest {

    @Mock
    private MovieRepository movieRepository;
    @Mock
    private MafApiService mafApiService;

    private Logger logger;

    private MovieUpdateEndpoint endpoint;

    @Before
    public void setUp() throws Exception {
        endpoint = new MovieUpdateEndpoint();
        insertMocks(endpoint, movieRepository, mafApiService);
        logger = mockLogger(endpoint);
    }

    @Test
    public void updateOldest() throws Exception {
        //Arrange
        Movie movie = new Movie();
        Container<Movie> container = new Container<>();
        when(movieRepository.findFirstByOrderByLastUpdatedAsc()).thenReturn(movie);
        when(mafApiService.updateMovie(any())).thenAnswer(invocation -> {
            container.setItem(getInvocationParam(invocation, 0));
            return container.getItem();
        });

        //Act
        endpoint.updateOldest();

        //Assert
        assertEquals(movie, container.getItem());
    }

    @Test
    public void updateMovie() throws Exception {
        //Arrange
        String imdbId = "imdbId";
        Movie movie = new Movie(imdbId);
        Container<Movie> container = new Container<>();
        when(movieRepository.findByImdbId(eq(imdbId))).thenReturn(movie);
        when(mafApiService.updateMovie(any())).thenAnswer(invocation -> {
            container.setItem(getInvocationParam(invocation, 0));
            return container.getItem();
        });

        //Act
        endpoint.updateMovie(imdbId);

        //Assert
        assertEquals(movie, container.getItem());
    }

    @Test
    public void updateMovieUsingCron() throws Exception {
        //Arrange
        Movie movie = new Movie("imdbId");
        movie.setTitle("title");
        Container<Movie> movieContainer = new Container<>();
        when(movieRepository.findFirstByOrderByLastUpdatedAsc()).thenReturn(movie);
        when(mafApiService.updateMovie(any())).thenAnswer(invocationOnMock -> {
            movieContainer.setItem(getInvocationParam(invocationOnMock, 0));
            return movieContainer.getItem();
        });

        //Act
        ReflectionTestUtils.invokeMethod(endpoint, "updateOldestByCron");

        //Assert
        verify(logger, times(1)).debug(anyString());
        verify(logger, times(1)).info(anyString(), eq(movie.getTitle()), eq(movie.getImdbId()));
        assertTrue(movieContainer.isPresent());
        assertEquals(movie, movieContainer.getItem());
    }

    @Test
    public void updateNonExistingMovieUsingCron() throws Exception {
        //Arrange
        when(movieRepository.findFirstByOrderByLastUpdatedAsc()).thenReturn(null);

        //Act
        ReflectionTestUtils.invokeMethod(endpoint, "updateOldestByCron");

        //Assert
        verify(logger, times(2)).debug(anyString());
        verify(mafApiService, times(0)).updateMovie(any());
    }

}