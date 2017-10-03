package nl.lijstr.repositories.movies.people;

import nl.lijstr.domain.movies.people.MovieWriter;
import nl.lijstr.repositories.abs.BasicMovieRepository;
import nl.lijstr.repositories.abs.PersonBoundRepository;

/**
 * A repository for Movie writers.
 */
public interface MovieWriterRepository extends BasicMovieRepository<MovieWriter>, PersonBoundRepository<MovieWriter> {

}
