package nl.lijstr.api.movies;

import nl.lijstr.api.abs.AbsService;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.services.maf.MafApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * A RestController that has endpoints for updating Movies.
 */
@RestController
@RequestMapping(value = "/movies/update", produces = "application/json")
public class MovieUpdateEndpoint extends AbsService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MafApiService mafApiService;

    /**
     * Update the movie that was last updated.
     */
    @RequestMapping(value = "/oldest", method = RequestMethod.PUT)
    public void updateOldest() {
        Movie movie = movieRepository.findFirstByOrderByLastUpdatedAsc();
        checkIfFound(movie);
        mafApiService.updateMovie(movie);
    }

    /**
     * Update a movie by it's IMDB ID.
     *
     * @param imdbId The ID of the movie
     */
    @RequestMapping(value = "/{imdbId:tt[0-9]+}", method = RequestMethod.PUT)
    public void updateMovie(@PathVariable("imdbId") String imdbId) {
        Movie movie = movieRepository.findByImdbId(imdbId);
        checkIfFound(movie, "Movie", imdbId);
        mafApiService.updateMovie(movie);
    }

}