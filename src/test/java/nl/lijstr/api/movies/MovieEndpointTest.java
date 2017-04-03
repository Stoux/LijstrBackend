package nl.lijstr.api.movies;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.lijstr.api.movies.models.MovieDetail;
import nl.lijstr.api.movies.models.MovieSummary;
import nl.lijstr.api.movies.models.post.PostedMovieRequest;
import nl.lijstr.beans.MovieAddBean;
import nl.lijstr.beans.UserBean;
import nl.lijstr.common.Container;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.domain.users.User;
import nl.lijstr.exceptions.security.UnauthorizedException;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.security.model.JwtGrantedAuthority;
import nl.lijstr.security.model.JwtUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static nl.lijstr._TestUtils.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Stoux on 24/04/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class MovieEndpointTest {

    public static final String IMDB_ID = "imdbId";
    public static final String YOUTUBE_ID = "youtubeId";
    @Mock
    private UserBean userBean;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private MovieAddBean addBean;

    private JwtUser jwtUser;
    private MovieEndpoint movieEndpoint;

    @Before
    public void setUp() throws Exception {
        movieEndpoint = new MovieEndpoint();
        insertMocks(movieEndpoint, movieRepository, addBean, userBean);

        LocalDateTime access = LocalDateTime.now().plusMinutes(5);
        jwtUser = new JwtUser(1L, "User", "Pass", new ArrayList<>(), access, access, 1L);
        when(userBean.getJwtUser()).thenReturn(jwtUser);
    }

    @Test
    public void getOne() throws Exception {
        //Arrange
        Movie movie = new Movie("", "", new User(2L));
        movie.setId(1L);
        movie.setOldSiteId(1L);
        when(movieRepository.findOne(eq(1L))).thenReturn(movie);

        //Act
        MovieDetail detail = movieEndpoint.getById(1L);

        //Assert
        assertEquals(1L, detail.getId());
        assertEquals(2L, detail.getAddedBy().longValue());
    }

    @Test
    public void getOriginal() throws Exception {
        //Arrange
        jwtUser.getAuthorities().add(new JwtGrantedAuthority(Permission.MOVIE_MOD));
        Movie movie = new Movie();
        when(movieRepository.findOne(eq(1L))).thenReturn(movie);

        //Act
        Movie originalById = movieEndpoint.getOriginalById(1L);

        //Assert
        assertEquals(movie, originalById);
    }

    @Test(expected = UnauthorizedException.class)
    public void getUnauthorizedOriginal() throws Exception {
        //Act
        movieEndpoint.getOriginalById(1L);

        //Assert
        fail("Shouldn't be allowed to get the movie");
    }

    @Test
    public void summaries() throws Exception {
        //Arrange
        Movie movie1 = createEmptyMovie(1);
        Movie movie2 = createEmptyMovie(2);
        Movie movie3 = createEmptyMovie(3);
        when(movieRepository.findAllByOrderByTitleAsc()).thenReturn(Arrays.asList(movie1, movie2, movie3));

        //Act
        List<MovieSummary> summaries = movieEndpoint.summaries(false, false,
                false, false, false, null);

        //Assert
        assertEquals(3, summaries.size());
        assertEquals(1L, summaries.get(0).getId());
        assertEquals(2L, summaries.get(1).getId());
        assertEquals(3L, summaries.get(2).getId());
    }

    @Test
    public void addMovie() throws Exception {
        //Arrange
        PostedMovieRequest postedRequest = new PostedMovieRequest(IMDB_ID, YOUTUBE_ID);
        Container<User> userContainer = new Container<>();
        when(addBean.addMovie(eq(IMDB_ID), eq(YOUTUBE_ID), any())).thenAnswer(i -> {
            userContainer.setItem(getInvocationParam(i, 2));
            return null;
        });

        //Act
        movieEndpoint.addMovie(postedRequest);

        //Assert
        verify(addBean, times(1)).checkIfMovieNotAdded(eq(IMDB_ID));
        verify(addBean, times(1)).getMovieData(eq(IMDB_ID));

        assertTrue(userContainer.isPresent());
        assertEquals(1L, userContainer.getItem().getId().longValue());
    }

    private Movie createEmptyMovie(int id) {
        Movie movie = new Movie("i" + id);
        movie.setId((long) id);
        movie.setTitle("t" + id);
        movie.setYear(id);
        movie.setLatestMovieRatings(new ArrayList<>());
        return movie;
    }

}