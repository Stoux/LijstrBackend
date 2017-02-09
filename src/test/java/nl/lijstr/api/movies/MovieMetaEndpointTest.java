package nl.lijstr.api.movies;

import nl.lijstr.api.movies.models.MovieUserMetaData;
import nl.lijstr.beans.UserBean;
import nl.lijstr.common.Container;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.movies.MovieUserMeta;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.repositories.movies.MovieUserMetaRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static nl.lijstr._TestUtils.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Stoux on 9-2-2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class MovieMetaEndpointTest {

    @Mock
    private UserBean userBean;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private MovieUserMetaRepository metaRepository;

    private Movie movie;
    private MovieMetaEndpoint endpoint;

    @Before
    public void setUp() throws Exception {
        endpoint = new MovieMetaEndpoint();
        insertMocks(endpoint, userBean, movieRepository, metaRepository);

        when(userBean.getJwtUser()).thenReturn(createUser(1L));

        movie = new Movie(null, null, null);
        movie.setId(1L);
        when(movieRepository.findOne(eq(1L))).thenReturn(movie);
    }

    @Test
    public void getForUser() throws Exception {
        //Arrange
        MovieUserMeta userMeta = new MovieUserMeta(true);
        when(metaRepository.findByMovieAndUser(eq(movie), any())).thenReturn(userMeta);

        //Act
        MovieUserMetaData forUser = endpoint.getForUser(1L);

        //Assert
        assertNotNull(forUser);
        assertTrue(forUser.isWantToWatch());
        verify(metaRepository, times(1)).findByMovieAndUser(eq(movie), any());
    }

    @Test
    public void getNewForUser() throws Exception {
        //Arrange
        when(metaRepository.findByMovieAndUser(any(), any())).thenReturn(null);

        //Act
        MovieUserMetaData forUser = endpoint.getForUser(1L);

        //Assert
        assertNotNull(forUser);
        assertFalse(forUser.isWantToWatch());
    }


    @Test
    public void updateExisting() throws Exception {
        //Arrange
        MovieUserMeta userMeta = new MovieUserMeta(true);
        when(metaRepository.findByMovieAndUser(eq(movie), any())).thenReturn(userMeta);

        MovieUserMetaData payload = new MovieUserMetaData(false);

        //Act
        endpoint.update(1L, payload);

        //Assert
        assertFalse(userMeta.isWantToWatch());
        verify(metaRepository, times(1)).saveAndFlush(eq(userMeta));
    }

    @Test
    public void updateNew() throws Exception {
        //Arrange
        when(metaRepository.findByMovieAndUser(any(), any())).thenReturn(null);
        Container<MovieUserMeta> container = new Container<>();
        when(metaRepository.saveAndFlush(any())).thenAnswer(i -> storeInvoked(i, container));

        MovieUserMetaData payload = new MovieUserMetaData(true);

        //Act
        endpoint.update(1L, payload);

        //Assert
        assertTrue(container.isPresent());
        MovieUserMeta userMeta = container.getItem();
        assertTrue(userMeta.isWantToWatch());
        assertEquals(movie, userMeta.getMovie());
        assertEquals(1L, userMeta.getUser().getId().longValue());
    }

}