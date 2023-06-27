package nl.lijstr.repositories.abs;

import nl.lijstr.domain.movies.Movie;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * Create a BasicMovieRepository.
 * This repository contains items that are linked to a movie.
 *
 * @param <T> The model
 */
@NoRepositoryBean
public interface BasicMovieRepository<T> extends BasicRepository<T> {

    /**
     * Find all items linked to a Movie.
     *
     * @param movie The movie
     *
     * @return the items
     */
    List<T> findByMovie(Movie movie);

}
