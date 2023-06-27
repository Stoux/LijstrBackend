package nl.lijstr.domain.interfaces;

import nl.lijstr.domain.movies.Movie;

/**
 * Indicates an object that is bound to a {@link Movie}
 */
public interface MovieBound {

    /**
     * Get the {@link Movie}
     *
     * @return the movie
     */
    Movie getMovie();

}
