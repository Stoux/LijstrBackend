package nl.lijstr.api.movies;

import java.util.ArrayList;
import nl.lijstr.api.movies.models.MovieSummary;
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
        movie.setId(1L);
        movie.setYear(1999);
        movie.setLatestMovieRatings(new ArrayList<>());

        Container<Movie> container = new Container<>();
        when(movieRepository.findFirstByOrderByLastUpdatedAsc()).thenReturn(movie);
        when(mafApiService.updateMovie(any())).thenAnswer(invocation -> {
            container.setItem(getInvocationParam(invocation, 0));
            return container.getItem();
        });

        //Act
        MovieSummary movieSummary = endpoint.updateOldest();

        //Assert
        assertEquals(movie, container.getItem());
        assertEquals(1L, movieSummary.getId());
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

}