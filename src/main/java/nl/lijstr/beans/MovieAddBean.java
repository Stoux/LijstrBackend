package nl.lijstr.beans;

import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.users.User;
import nl.lijstr.exceptions.BadRequestException;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.services.maf.MafApiService;
import nl.lijstr.services.omdb.OmdbApiService;
import nl.lijstr.services.omdb.models.OmdbObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Bean for utility methods regarding the ability to add movies.
 */
@Component
public class MovieAddBean {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private OmdbApiService omdbApiService;

    @Autowired
    private MafApiService mafApiService;

    /**
     * Check if a movie is already added to the DB.
     *
     * @param imdbId The IMDB ID
     */
    public void checkIfMovieNotAdded(String imdbId) {
        if (movieRepository.findByImdbId(imdbId) != null) {
            throw new BadRequestException("Movie already added");
        }
    }

    /**
     * Check if the movie exists and fetch it's data.
     *
     * @param imdbId The IMDB ID
     *
     * @return the resulting data object
     */
    public OmdbObject getMovieData(String imdbId) {
        return omdbApiService.getMovie(imdbId);
    }


    /**
     * Add a movie to the DB.
     *
     * @param imdbId    The IMDB ID
     * @param youtubeId The Youtube ID
     * @param addedBy   Added by
     *
     * @return the added movie
     */
    public Movie addMovie(String imdbId, String youtubeId, User addedBy) {
        //Add the movie
        Movie newMovie = new Movie(imdbId, youtubeId, addedBy);
        final Movie movie = movieRepository.save(newMovie);
        return mafApiService.updateMovie(movie);
    }

}
