package nl.lijstr.repositories.imdb;

import nl.lijstr.domain.imdb.Genre;
import nl.lijstr.repositories.abs.BasicRepository;

/**
 * A basic IMDB Genre repository.
 */
public interface GenreRepository extends BasicRepository<Genre> {

    /**
     * Get a Genre object by it's genre.
     *
     * @param genre The genre
     *
     * @return the genre object or null
     */
    Genre getByGenre(String genre);

}
