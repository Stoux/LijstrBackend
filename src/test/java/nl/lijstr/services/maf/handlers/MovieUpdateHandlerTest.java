package nl.lijstr.services.maf.handlers;

import com.google.gson.Gson;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import nl.lijstr._TestUtils.TestUtils;
import nl.lijstr.beans.ImdbBean;
import nl.lijstr.domain.imdb.Genre;
import nl.lijstr.domain.imdb.Person;
import nl.lijstr.domain.imdb.SpokenLanguage;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.movies.people.MovieCharacter;
import nl.lijstr.domain.other.FieldHistory;
import nl.lijstr.exceptions.LijstrException;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.repositories.other.FieldHistoryRepository;
import nl.lijstr.repositories.other.FieldHistorySuggestionRepository;
import nl.lijstr.services.maf.handlers.util.FieldModifyHandler;
import nl.lijstr.services.maf.models.ApiAka;
import nl.lijstr.services.maf.models.ApiMovie;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import static nl.lijstr._TestUtils.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * A test for updating movies.
 * <p>
 * Note:
 * This test relies heavily on {@link nl.lijstr.services.maf.handlers.util.FieldModifyHandler} and it's test.
 * If anything of that test fails; this test will fail as well.
 */
@RunWith(MockitoJUnitRunner.class)
public class MovieUpdateHandlerTest {

    private MovieUpdateHandler updateHandler;

    @Mock
    private FieldHistoryRepository historyMock;
    @Mock
    private FieldHistorySuggestionRepository suggestionMock;
    @Mock
    private MovieRepository movieMock;
    @Mock
    private ImdbBean imdbMock;
    private Logger loggerMock;

    @Before
    public void setUp() throws Exception {
        updateHandler = new MovieUpdateHandler(movieMock, historyMock, suggestionMock, imdbMock, null);
        loggerMock = mockLogger(updateHandler);
    }

    @Test
    public void update() throws Exception {
        //Arrange
        Movie movie = createMovie("tt3659388");
        ApiMovie apiMovie = loadApiMovie();

        SpokenLanguage language = new SpokenLanguage("English");
        SpokenLanguage incorrectLanguage = new SpokenLanguage("Blergh");
        movie.getLanguages().addAll(Arrays.asList(language, incorrectLanguage));

        String mattImdb = "nm0000354";
        Person mattDamon = new Person(mattImdb, "Matt Damon");
        MovieCharacter markIncorrect = new MovieCharacter(movie, mattDamon, "Mark Incorrect", "", "", false);
        ReflectionTestUtils.setField(markIncorrect, "id", 1L);
        movie.getCharacters().add(markIncorrect);

        String seanImdb = "nm0000293";
        Person seanBean = new Person(seanImdb, "Sean Bean");


        //Arrange mocks
        when(movieMock.saveAndFlush(any()))
                .thenAnswer(invocation -> invocation.getArguments()[0]);

        when(historyMock.findByObjectIdAndClassName(anyLong(), anyString()))
                .thenReturn(new ArrayList<>());

        when(imdbMock.getOrCreateGenre(anyString()))
                .thenAnswer(invocation -> new Genre((String) invocation.getArguments()[0]));
        when(imdbMock.getOrCreateLanguage(anyString()))
                .thenAnswer(invocation -> new SpokenLanguage((String) invocation.getArguments()[0]));
        when(imdbMock.getPerson(anyString()))
                .thenReturn(null);
        when(imdbMock.getPerson(mattImdb))
                .thenReturn(mattDamon);
        when(imdbMock.getPerson(seanImdb))
                .thenReturn(seanBean);
        when(imdbMock.addPerson(any()))
                .thenAnswer(invocation -> invocation.getArguments()[0]);

        //Act
        Movie updatedMovie = updateHandler.update(movie, apiMovie);

        //Assert - Movie details
        assertEquals(movie, updatedMovie);
        //assertEquals("The Martian", movie.getTitle());
        assertEquals("De Marsman", movie.getDutchTitle());
        assertNull(movie.getOriginalTitle());
        assertEquals(Integer.valueOf(2015), movie.getYear());
        assertEquals(LocalDate.of(2015, 10, 2), movie.getReleased());
        assertEquals(Integer.valueOf(144), movie.getRuntime());
        assertFalse(movie.isPoster());
        assertEquals(Double.valueOf(8.1), movie.getImdbRating());
        assertEquals(Integer.valueOf(80), movie.getMetacriticScore());
        assertEquals("PG-13", movie.getAgeRating());
        assertEquals(Long.valueOf(398101), movie.getImdbVotes());

        assertEquals(2, movie.getLanguages().size());
        assertEquals(3, movie.getGenres().size());
        assertEquals(121, movie.getTrivia().size());
        assertEquals(2, movie.getWriters().size());
        assertEquals(1, movie.getDirectors().size());
        assertEquals(15, movie.getCharacters().size());

        //Language fixed
        assertTrue(movie.getLanguages().contains(language));
        assertFalse(movie.getLanguages().contains(incorrectLanguage));

        //People
        assertTrue("Should have used the given object", movie.getCharacters().contains(markIncorrect));
        assertEquals("Mark Watney", markIncorrect.getCharacter());
        assertEquals(true, markIncorrect.isMainCharacter());

        MovieCharacter seanChar = findInList(movie.getCharacters(), seanImdb, (x, y) -> x.equals(y.getImdbId()));
        assertEquals(seanBean, seanChar.getPerson());
    }

    @Test(expected = LijstrException.class)
    public void updateWrongMovie() throws Exception {
        //Arrange
        Movie movie = new Movie("tt0000000");
        ApiMovie apiMovie = loadApiMovie();

        //Act
        updateHandler.update(movie, apiMovie);

        //Assert
        fail("Should have thrown a LijstrException as the IMDB IDs are not equal");
    }

    @Ignore //Ignore as the title is temporary fetched from OmdbAPI
    @Test
    public void frenchTitleUpdate() {
        //Arrange
        Movie movie = createMovie("tt3659388");
        ApiMovie apiMovie = loadApiMovie();

        String originalTitle = apiMovie.getTitle();
        String frenchTitle = "French is the worst";

        movie.setOriginalTitle(originalTitle);
        movie.setTitle(frenchTitle);

        ApiAka french = new ApiAka();
        ReflectionTestUtils.setField(french, "country", "France");
        ReflectionTestUtils.setField(french, "title", frenchTitle);

        apiMovie.getAkas().clear();
        apiMovie.getAkas().add(french);

        FieldModifyHandler fieldHandler = mock(FieldModifyHandler.class);
        when(historyMock.saveAndFlush(any())).thenAnswer(i -> {
            FieldHistory h = getInvocationParam(i, 0);
            assertEquals(h.getClassName(), FieldHistory.getDatabaseClassName(Movie.class));
            assertEquals(h.getObjectId(), movie.getId());
            assertEquals(h.getOldValue(), frenchTitle);
            assertEquals(h.getNewValue(), originalTitle);
            return h;
        });

        //Act
        ReflectionTestUtils.invokeMethod(
                updateHandler, "updateTitles",
                fieldHandler, movie, apiMovie
        );

        //Assert
        verify(historyMock, times(1)).saveAndFlush(any());
        assertEquals(originalTitle, movie.getTitle());
    }

    private Movie createMovie(String imdbId) {
        Movie movie = new Movie(imdbId);
        ReflectionTestUtils.setField(movie, "id", 1L);
        for (Field field : Movie.class.getDeclaredFields()) {
            if (List.class.isAssignableFrom(field.getType())) {
                ReflectionTestUtils.setField(movie, field.getName(), new ArrayList<>());
            }
        }
        return movie;
    }

    private ApiMovie loadApiMovie() {
        InputStreamReader reader = new InputStreamReader(TestUtils.getTestResource("maf/movie.json"));
        ApiMovie apiMovie = new Gson().fromJson(reader, ApiMovie.class);
        assertNotNull(apiMovie);
        return apiMovie;
    }

    private <X, Y> X findInList(List<X> list, Y item, BiFunction<Y, X, Boolean> isSame) {
        for (X otherItem : list) {
            if (isSame.apply(item, otherItem)) {
                return otherItem;
            }
        }
        fail("Item was not found");
        return null;
    }

}