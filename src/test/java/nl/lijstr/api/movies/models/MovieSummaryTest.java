package nl.lijstr.api.movies.models;

import java.math.BigDecimal;
import java.util.*;
import nl.lijstr.domain.imdb.Genre;
import nl.lijstr.domain.imdb.SpokenLanguage;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.movies.MovieRating;
import nl.lijstr.domain.users.User;
import org.junit.Test;

import static nl.lijstr._TestUtils.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Stoux on 25/04/2016.
 */
public class MovieSummaryTest {

    @Test
    public void convertWithAll() throws Exception {
        //Arrange
        Movie movie = createDefaultMovie();
        addRating(movie, 1L);

        //Act
        MovieSummary summary = MovieSummary.convert(
            movie, false, false, true, true, true, null
        );
        new MovieSummary(); //Coverage lol

        //Assert
        assertEquals(1L, summary.getId());
        assertEquals("imdbId", summary.getImdbId());
        assertEquals("Title", summary.getTitle());
        assertEquals(1337, summary.getYear());
        assertEquals("Age Rating", summary.getAgeRating());
        assertEquals(50, summary.getRuntime().intValue());
        assertEquals(5.2, summary.getImdbRating(), 0.01);
        assertEquals(99, summary.getMetacriticScore().intValue());

        assertEquals(1, summary.getLatestRatings().size());
        assertNotNull(summary.getLatestRatings().get(1L));

        Map<Long, String> genres = summary.getGenres();
        assertEquals(2, genres.size());
        assertEquals("Genre 1", genres.get(1L));

        Map<Long, String> languages = summary.getLanguages();
        assertEquals(2, languages.size());
        assertEquals("Language 2", languages.get(2L));
    }

    @Test
    public void convertOtherTitles() throws Exception {
        //Arrange
        Movie movie = createDefaultMovie();

        //Act
        MovieSummary dutch = MovieSummary.convert(
            movie, true, false, false, false, false, null
        );
        MovieSummary original = MovieSummary.convert(
            movie, false, true, false, false, false, null
        );

        //Assert
        assertEquals("Dutch", dutch.getTitle());
        assertEquals("Original", original.getTitle());
    }

    @Test
    public void convertMinimum() throws Exception {
        //Arrange
        Movie movie = createDefaultMovie();
        addRating(movie, 1L);

        //Act
        MovieSummary summary = MovieSummary.convert(
            movie, false, false, false, false, false, Collections.emptySet()
        );

        //Assert
        assertNull(summary.getGenres());
        assertNull(summary.getLanguages());
        assertNull(summary.getAgeRating());
        assertNull(summary.getLatestRatings());
    }

    @Test
    public void convertWithUsers()  throws Exception {
        //Arrange
        Movie movie = createDefaultMovie();
        addRating(movie, 1L);
        addRating(movie, 2L);
        addRating(movie, 3L);

        Set<Long> requestedUsers = new HashSet<>(Arrays.asList(1L, 3L));

        //Act
        MovieSummary summary = MovieSummary.convert(
            movie, false, false, false, false, false, requestedUsers
        );

        //Assert
        Map<Long, MovieShortRating> ratings = summary.getLatestRatings();
        assertEquals(2, ratings.size());
        assertTrue(ratings.containsKey(1L));
        assertEquals(1L, ratings.get(1L).getUser());
        assertTrue(ratings.containsKey(3L));
    }

    private static Movie createDefaultMovie() {
        //Arrange
        Genre genre1 = new Genre("Genre 1");
        genre1.setId(1L);
        Genre genre2 = new Genre("Genre 2");
        genre2.setId(2L);
        SpokenLanguage language1 = new SpokenLanguage("Language 1");
        language1.setId(1L);
        SpokenLanguage language2 = new SpokenLanguage("Language 2");
        language2.setId(2L);

        Movie movie = new Movie("imdbId");
        movie.setId(1L);
        movie.setTitle("Title");
        movie.setYear(1337);
        movie.setRuntime(50);
        movie.setImdbRating(5.2);
        movie.setMetacriticScore(99);
        movie.setDutchTitle("Dutch");
        movie.setOriginalTitle("Original");
        movie.setLatestMovieRatings(new ArrayList<>());
        movie.setAgeRating("Age Rating");
        movie.setGenres(Arrays.asList(genre1, genre2));
        movie.setLanguages(Arrays.asList(language1, language2));

        return movie;
    }

    private static void addRating(Movie movie, long user) {
        movie.getLatestMovieRatings().add(new MovieRating(
            movie, new User(user), MovieRating.Seen.YES, 1.0, null
        ));
    }

}
