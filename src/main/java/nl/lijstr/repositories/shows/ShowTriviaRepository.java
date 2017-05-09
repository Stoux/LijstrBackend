package nl.lijstr.repositories.shows;

import nl.lijstr.domain.shows.Show;
import nl.lijstr.domain.shows.ShowTrivia;
import nl.lijstr.repositories.abs.BasicMovieRepository;
import nl.lijstr.repositories.abs.BasicRepository;
import nl.lijstr.repositories.abs.BasicShowRepository;

/**
 * Repository for a show's trivia.
 */
public interface ShowTriviaRepository extends BasicShowRepository<ShowTrivia> {

}
