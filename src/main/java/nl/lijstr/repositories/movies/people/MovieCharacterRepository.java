package nl.lijstr.repositories.movies.people;

import nl.lijstr.domain.movies.people.MovieCharacter;
import nl.lijstr.repositories.abs.BasicMovieRepository;
import nl.lijstr.repositories.abs.PersonBoundRepository;

/**
 * A repository for Movie characters.
 */
public interface MovieCharacterRepository extends BasicMovieRepository<MovieCharacter>,
    PersonBoundRepository<MovieCharacter> {

}
