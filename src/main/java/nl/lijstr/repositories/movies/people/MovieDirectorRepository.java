package nl.lijstr.repositories.movies.people;

import nl.lijstr.domain.movies.people.MovieDirector;
import nl.lijstr.repositories.abs.BasicMovieRepository;
import nl.lijstr.repositories.abs.PersonBoundRepository;

/**
 * A repository for Movie directors.
 */
public interface MovieDirectorRepository extends BasicMovieRepository<MovieDirector>,
    PersonBoundRepository<MovieDirector> {

}
