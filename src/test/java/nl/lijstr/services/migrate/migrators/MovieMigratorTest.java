package nl.lijstr.services.migrate.migrators;

import nl.lijstr.common.Container;
import nl.lijstr.common.Utils;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.services.maf.MafApiService;
import nl.lijstr.services.migrate.models.MigrationProgress;
import nl.lijstr.services.migrate.models.movies.OldMovie;
import nl.lijstr.services.migrate.retrofit.OldSiteService;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static nl.lijstr._TestUtils.TestUtils.getInvocationParam;
import static nl.lijstr._TestUtils.TestUtils.successCall;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Stoux on 1-2-2017.
 */
public class MovieMigratorTest {

    private static final String IMDB_LINK = "http://www.imdb.com/title/tt0000001/";

    private MovieMigrator migrator;

    private OldSiteService oldSiteService;
    private MovieRepository movieRepository;
    private MafApiService mafApiService;

    private MigrationProgress progress;
    private List<Movie> saveCalls;

    @Before
    public void setUp() throws Exception {
        progress = new MigrationProgress();
        saveCalls = new ArrayList<>();

        oldSiteService = mock(OldSiteService.class);
        movieRepository = mock(MovieRepository.class);
        mafApiService = mock(MafApiService.class);

        migrator = new MovieMigrator(
                oldSiteService, movieRepository, mafApiService, progress,
                mock(Logger.class)
        );

        when(movieRepository.save(any(Movie.class))).thenAnswer(i -> {
            Movie movie = getInvocationParam(i, 0);
            saveCalls.add(movie);
            Movie newMovie = new Movie();
            ReflectionUtils.shallowCopyFieldState(movie, newMovie);
            return newMovie;
        });
    }

    @Test
    public void migrateExisting() throws Exception {
        //Arrange
        Movie movie = new Movie("tt0000001", "Title", 1994, 10L);
        movie.setId(1L);
        OldMovie oldMovie = new OldMovie(10L, "Title X", IMDB_LINK);

        whenFindMovies(movie);
        whenGetOldMovies(oldMovie);

        //Act
        migrator.migrate();

        //Assert
        assertNonFailedProgress();
        assertTrue(progress.getUpdated().isEmpty());
        assertTrue(progress.getAdded().isEmpty());
        assertTrue(progress.getAddedAndFilled().isEmpty());

        verify(movieRepository, times(0)).save(any(Movie.class));
        verify(mafApiService, times(0)).updateMovie(any());
    }

    @Test
    public void migrateExistingWithoutId() throws Exception {
        //Arrange
        Movie movie = new Movie("tt0000001", "Title", 1994, 0L);
        movie.setId(1L);
        OldMovie oldMovie = new OldMovie(10L, "Title X", IMDB_LINK);
        assertNotEquals(movie.getOldSiteId(), oldMovie.getId());

        whenFindMovies(movie);
        whenGetOldMovies(oldMovie);

        //Act
        migrator.migrate();

        //Assert
        assertNonFailedProgress();
        assertEquals(oldMovie.getId(), movie.getOldSiteId());
        assertEquals(1, progress.getUpdated().size());
        assertEquals(movie.getTitle(), progress.getUpdated().get(movie.getImdbId()));

        verify(movieRepository, times(1)).save(eq(movie));
        verify(mafApiService, times(0)).updateMovie(any());
    }

    @Test
    public void migrateNewEntry() throws Exception {
        //Arrange
        OldMovie oldMovie = new OldMovie(10L, "Title X", IMDB_LINK);
        Container<Movie> movieContainer = new Container<>();

        whenFindMovies();
        whenGetOldMovies(oldMovie);
        when(mafApiService.updateMovie(any())).thenAnswer(i -> {
            Movie movie = getInvocationParam(i, 0);
            movieContainer.setItem(movie);
            return movie;
        });

        //Act
        migrator.migrate();

        //Assert
        assertNonFailedProgress();
        verify(movieRepository, times(1)).save(any(Movie.class));
        verify(mafApiService, times(1)).updateMovie(any());

        assertEquals(1, saveCalls.size());
        assertNotNull(movieContainer.getItem());
        assertNotEquals(
                "MovieRepository#save() can return a different object",
                saveCalls.get(0), movieContainer.getItem()
        );
        assertEquals(saveCalls.get(0).getId(), movieContainer.getItem().getId());
        assertEquals("tt0000001", saveCalls.get(0).getImdbId());
    }

    @Test
    public void migrateFail() throws Exception {
        //Arrange
        RuntimeException exception = new RuntimeException();
        when(oldSiteService.listMovies()).thenThrow(exception);

        //Act
        migrator.migrate();

        //Assert
        assertTrue(progress.isFailed());
        assertEquals(exception, progress.getException());
    }

    private void assertNonFailedProgress() {
        assertTrue(progress.isFinished());
        if (progress.getException() != null) {
            progress.getException().printStackTrace();
        }
        assertFalse(progress.isFailed());
    }

    private void whenFindMovies(Movie... movies) {
        List<Movie> movieList = Arrays.asList(movies);
        when(movieRepository.findAll()).thenReturn(movieList);
    }

    private void whenGetOldMovies(OldMovie... movies) {
        Map<Long, OldMovie> movieMap = Utils.toMap(Arrays.asList(movies), OldMovie::getId);
        when(oldSiteService.listMovies()).thenAnswer(i -> successCall(movieMap));
    }

}