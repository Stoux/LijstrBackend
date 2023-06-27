package nl.lijstr.services.omdb;

import nl.lijstr._TestUtils.TestUtils;
import nl.lijstr.exceptions.BadRequestException;
import nl.lijstr.services.omdb.models.OmdbObject;
import nl.lijstr.services.omdb.retrofit.OmdbService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import static nl.lijstr._TestUtils.TestUtils.insertMocks;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Created by Stoux on 23/04/2016.
 */
public class OmdbServiceTest {

    private OmdbApiService apiService;
    private OmdbService omdbService;

    @BeforeEach
    public void setUp() throws Exception {
        apiService = new OmdbApiService("");

        omdbService = mock(OmdbService.class);
        insertMocks(apiService, omdbService);
    }

    @Test
    public void getMovie() throws Exception {
        //Arrange
        OmdbObject omdbObject = new OmdbObject("", "", "", "movie");
        Call<OmdbObject> mockedCall = TestUtils.successCall(omdbObject);
        when(omdbService.getByImdbId(anyString(), anyString()))
                .thenReturn(mockedCall);

        //Act
        OmdbObject movie = apiService.getMovie("Movie");

        //Assert
        assertEquals(omdbObject, movie);
    }

    @Test()
    public void getNonMovie() throws Exception {
        //Arrange
        OmdbObject omdbObject = new OmdbObject("", "", "", "series");
        Call<OmdbObject> mockedCall = TestUtils.successCall(omdbObject);
        when(omdbService.getByImdbId(anyString(), anyString()))
                .thenReturn(mockedCall);

        //Act
        assertThrows(
                BadRequestException.class,
                () -> apiService.getMovie("Series"),
                "The OmdbObject isn't a movie"
        );
    }

    @Test()
    public void getNonExistentMovie() throws Exception {
        //Arrange
        Call<OmdbObject> failedCall = TestUtils.failedCall(404, "Not found");
        when(omdbService.getByImdbId(anyString(), anyString()))
                .thenReturn(failedCall);

        //Act
        assertThrows(
                BadRequestException.class,
                () -> apiService.getMovie("Null"),
                "The movie didn't exist"
        );
    }

    @Test
    public void equalTest() {
        //Arrange
        OmdbObject movie = new OmdbObject("", "", "", "movie");
        OmdbObject series = new OmdbObject("", "", "", "series");

        //Act
        boolean isMovie = movie.isMovie();
        boolean notASeries = movie.isSeries();
        boolean isSeries = series.isSeries();
        boolean notAMovie = series.isMovie();

        //Assert
        assertTrue(isMovie);
        assertTrue(isSeries);
        assertFalse(notASeries);
        assertFalse(notAMovie);
    }

}