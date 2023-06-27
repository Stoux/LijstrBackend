package nl.lijstr.beans;

import nl.lijstr._TestUtils.TestUtils;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.users.User;
import nl.lijstr.exceptions.BadRequestException;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.services.maf.MafApiService;
import nl.lijstr.services.omdb.OmdbApiService;
import nl.lijstr.services.omdb.models.OmdbObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static nl.lijstr._TestUtils.TestUtils.insertMocks;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Stoux on 6-2-2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class MovieAddBeanTest {

    private static final String IMDB_ID = "tt0481522";
    private static final String TITLE = "Movie";
    public static final String YOUTUBE = "YOUTUBE";

    @Mock
    private MovieRepository movieRepository;
    @Mock
    private OmdbApiService omdbApiService;
    @Mock
    private MafApiService mafApiService;

    private MovieAddBean addBean;

    @Before
    public void setUp() throws Exception {
        this.addBean = new MovieAddBean();
        insertMocks(addBean, movieRepository, omdbApiService, mafApiService);
    }

    @Test
    public void noMovieAdded() throws Exception {
        //Arrange
        when(movieRepository.findByImdbId(eq(IMDB_ID))).thenReturn(null);

        //Act
        addBean.checkIfMovieNotAdded(IMDB_ID);

        //Assert
        verify(movieRepository, times(1)).findByImdbId(eq(IMDB_ID));
    }

    @Test(expected = BadRequestException.class)
    public void movieAdded() throws Exception {
        //Arrange
        when(movieRepository.findByImdbId(eq(IMDB_ID))).thenReturn(new Movie());

        //Act
        addBean.checkIfMovieNotAdded(IMDB_ID);

        //Assert
        fail("A BadRequestException should have been thrown as the movie is already added.");
    }

    @Test
    public void getMovieData() throws Exception {
        //Arrange
        OmdbObject o = new OmdbObject();
        when(omdbApiService.getMovie(IMDB_ID)).thenReturn(o);

        //Act
        OmdbObject movieData = addBean.getMovieData(IMDB_ID);

        //Assert
        assertNotNull(movieData);
        assertEquals(o, movieData);
    }

    @Test
    public void addMovie() throws Exception {
        //Arrange
        User u = new User(1);
        when(movieRepository.save(any(Movie.class)))
                .thenAnswer(i -> TestUtils.<Movie>copyInvokedAndModify(i, m -> m.setId(1L)));
        when(mafApiService.updateMovie(any()))
                .thenAnswer(i -> TestUtils.<Movie>copyInvokedAndModify(i, m -> m.setOldSiteId(1L)));

        //Act
        Movie movie = addBean.addMovie(IMDB_ID, TITLE, YOUTUBE, u);

        //Assert
        assertNotNull(movie);
        assertEquals(IMDB_ID, movie.getImdbId());
        assertEquals(YOUTUBE, movie.getYoutubeUrl());
        assertEquals(TITLE, movie.getTitle());
        assertEquals(u, movie.getAddedBy());

        assertEquals(1L, movie.getId().longValue());
        assertEquals(1L, movie.getOldSiteId().longValue());

        verify(movieRepository, times(1)).save(any(Movie.class));
        verify(mafApiService, times(1)).updateMovie(any());
    }


}