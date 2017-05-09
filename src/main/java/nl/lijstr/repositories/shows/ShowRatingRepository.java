package nl.lijstr.repositories.shows;

import nl.lijstr.domain.shows.Show;
import nl.lijstr.domain.shows.ShowRating;
import nl.lijstr.repositories.abs.BasicRatingRepository;

/**
 * A repository for {@link ShowRating}s.
 */
public interface ShowRatingRepository extends BasicRatingRepository<Show, ShowRating> {

}
