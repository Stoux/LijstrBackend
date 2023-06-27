package nl.lijstr.api.movies;

import nl.lijstr.api.movies.models.MovieDetail;
import nl.lijstr.api.movies.models.wrappers.MovieOutstandingCount;
import nl.lijstr.beans.UserBean;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.movies.MovieRating;
import nl.lijstr.domain.users.User;
import nl.lijstr.repositories.movies.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static nl.lijstr._TestUtils.TestUtils.createUser;
import static nl.lijstr._TestUtils.TestUtils.insertMocks;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Created by Stoux on 8-2-2017.
 */
@ExtendWith(MockitoExtension.class)
public class MovieOutstandingEndpointTest {

    @Mock
    private UserBean userBean;
    @Mock
    private MovieRepository movieRepository;

    private List<Movie> movies;

    private MovieOutstandingEndpoint endpoint;

    @BeforeEach
    public void setUp() throws Exception {
        endpoint = new MovieOutstandingEndpoint();
        insertMocks(endpoint, movieRepository, userBean);

        movies = Arrays.asList(
                createMovie(1L, 1L, 2L, 3L),
                createMovie(2L, 2L, 3L),
                createMovie(3L, 1L, 3L),
                createMovie(4L, 2L)
        );
        when(movieRepository.findAllByOrderByTitleAsc()).thenReturn(movies);
        when(userBean.getJwtUser()).thenReturn(createUser(1L));
    }

    @Test
    public void countWithoutRating() throws Exception {
        //Act
        MovieOutstandingCount movieOutstandingCount = endpoint.countWithoutRating();

        //Assert
        assertNotNull(movieOutstandingCount);
        assertEquals(2L, movieOutstandingCount.getTotal());
    }

    @Test
    public void getWithoutRating() throws Exception {
        //Act
        List<MovieDetail> withoutRating = endpoint.getWithoutRating();

        //Assert
        assertEquals(2, withoutRating.size());
        assertEquals(2L, withoutRating.get(0).getId());
        assertEquals(4L, withoutRating.get(1).getId());
    }

    private Movie createMovie(long id, long... withUsers) {
        Movie m = new Movie();
        m.setId(id);
        m.setOldSiteId(id);

        List<MovieRating> movieRatings = new ArrayList<>();
        for (long withUser : withUsers) {
            movieRatings.add(new MovieRating(
                    m, new User(withUser),
                    MovieRating.Seen.NO, null, null
            ));
        }

        m.setLatestMovieRatings(movieRatings);
        m.setWriters(new ArrayList<>());
        m.setDirectors(new ArrayList<>());

        return m;
    }


}