package nl.lijstr.services.maf;

import lombok.SneakyThrows;
import nl.lijstr.common.Utils;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.exceptions.LijstrException;
import nl.lijstr.services.maf.handlers.MovieUpdateHandler;
import nl.lijstr.services.maf.models.ApiMovie;
import nl.lijstr.services.maf.models.containers.ApiMovieModel;
import nl.lijstr.services.maf.retrofit.ImdbService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

import static nl.lijstr._TestUtils.TestUtils.insertMocks;
import static nl.lijstr._TestUtils.TestUtils.mockLogger;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Created by Stoux on 16/04/2016.
 */
public class MafApiServiceTest {

    private MafApiService mafApiService;
    private MovieUpdateHandler updateHandler;
    private ImdbService imdbService;

    @BeforeEach
    public void setUp() throws Exception {
        mafApiService = new MafApiService();
        mockLogger(mafApiService);
        updateHandler = mock(MovieUpdateHandler.class);
        imdbService = mock(ImdbService.class);
        insertMocks(mafApiService, updateHandler, imdbService);
    }

    @Test
    public void updateMovie() throws Exception {
        //Arrange
        String imdbId = "tt0000000";
        Movie movie = new Movie(imdbId);
        ApiMovie apiMovie = new ApiMovie();
        ApiMovieModel apiModel = mock(ApiMovieModel.class);
        when(apiModel.getMovie()).thenReturn(apiMovie);

        Call<ApiMovieModel> mockedCall = mock(Call.class);
        Response<ApiMovieModel> mockedResponse = Response.success(apiModel);
        doReturn(mockedResponse).when(mockedCall).execute();

        assertNotNull(imdbService);
        when(imdbService.getMovie(any(), eq(imdbId), anyString(), anyString(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn(mockedCall);
        when(updateHandler.update(any(), any()))
                .thenReturn(movie);

        //Act
        Movie resultingMovie = mafApiService.updateMovie(movie);

        //Assert
        assertEquals(movie, resultingMovie);
        verify(imdbService, times(1)).getMovie(any(), eq(imdbId), anyString(), anyString(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt());
        verify(updateHandler, times(1)).update(movie, apiMovie);
    }

    @SneakyThrows
    @Test()
    public void executeFailedCall() {
        //Arrange
        Call call = mock(Call.class);
        doThrow(new IOException()).when(call).execute();

        assertThrows(
                LijstrException.class,
                () -> Utils.executeCall(call),
                "IOException should have been caught and rethrown as a LijstrException."
        );
    }

}