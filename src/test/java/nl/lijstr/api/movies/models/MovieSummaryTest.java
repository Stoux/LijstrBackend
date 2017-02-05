package nl.lijstr.api.movies.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import nl.lijstr.domain.imdb.Genre;
import nl.lijstr.domain.imdb.SpokenLanguage;
import nl.lijstr.domain.movies.Movie;
import org.junit.Test;

import static nl.lijstr._TestUtils.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Stoux on 25/04/2016.
 */
public class MovieSummaryTest {

    @Test
    public void convert() throws Exception {
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
        movie.setLatestMovieRatings(new ArrayList<>());
        movie.setAgeRating("Age Rating");
        movie.setGenres(Arrays.asList(genre1, genre2));
        movie.setLanguages(Arrays.asList(language1, language2));

        //Act
        MovieSummary summary = MovieSummary.convert(movie, false, false, true, true, true);
        new MovieSummary(); //Coverage lol

        //Assert
        assertEquals(movie.getId().longValue(), summary.getId());
        assertEquals(movie.getImdbId(), summary.getImdbId());
        assertEquals(movie.getTitle(), summary.getTitle());
        assertEquals(movie.getYear().intValue(), summary.getYear());
        assertEquals(movie.getAgeRating(), summary.getAgeRating());
        assertEquals(0, summary.getLatestRatings().size());

        Map<Long, String> genres = summary.getGenres();
        assertEquals(2, genres.size());
        assertEquals(genre1.getGenre(), genres.get(genre1.getId()));
        assertEquals(genre2.getGenre(), genres.get(genre2.getId()));

        Map<Long, String> languages = summary.getLanguages();
        assertEquals(2, languages.size());
        assertEquals(language1.getLanguage(), languages.get(language1.getId()));
        assertEquals(language2.getLanguage(), languages.get(language2.getId()));
    }

}
