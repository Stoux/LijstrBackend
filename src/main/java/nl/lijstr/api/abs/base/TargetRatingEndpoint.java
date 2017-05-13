package nl.lijstr.api.abs.base;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.validation.Valid;
import nl.lijstr.api.abs.AbsService;
import nl.lijstr.api.abs.base.models.ExtendedRating;
import nl.lijstr.api.abs.base.models.post.RatingRequest;
import nl.lijstr.common.DataContainer;
import nl.lijstr.domain.base.RatingModel;
import nl.lijstr.domain.interfaces.Target;
import nl.lijstr.domain.users.User;
import nl.lijstr.exceptions.db.ConflictException;
import nl.lijstr.repositories.abs.BasicRatingRepository;
import nl.lijstr.repositories.abs.BasicTargetRepository;
import nl.lijstr.security.model.JwtUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Base endpoint for ratings on a {@link nl.lijstr.domain.interfaces.Target}.
 */
public abstract class TargetRatingEndpoint<T extends Target, X extends RatingModel<T>, EX extends ExtendedRating, RQ extends RatingRequest> extends
    AbsService {

    /**
     * The number of minutes that is considered recent.
     * Used to force user to edit and/or add.
     */
    public static final long RECENT_MINUTES = 30L;

    private final BasicTargetRepository<T> targetRepository;
    private final BasicRatingRepository<T, X> ratingRepository;

    private final String name;

    public TargetRatingEndpoint(BasicTargetRepository<T> targetRepository, BasicRatingRepository<T, X> ratingRepository,
                                String name) {
        this.targetRepository = targetRepository;
        this.ratingRepository = ratingRepository;
        this.name = name;
    }

    /**
     * Get the latest rating a user has given on a target.
     *
     * @param id The target ID
     *
     * @return a container containing the rating or null (if the user hasn't given a rating)
     */
    @RequestMapping(path = "/latest/")
    public DataContainer<EX> getLatestRatingForUser(@PathVariable Long id) {
        T target = findOne(targetRepository, id, name);
        JwtUser user = getUser();
        X rating = ratingRepository.findByTargetAndUserAndLatestIsTrue(target, new User(user.getId()));
        return new DataContainer<>(rating == null ? null : convertRating(rating));
    }

    /**
     * Add a new rating.
     *
     * @param id        The target's ID
     * @param newRating The new Rating
     *
     * @return a short version of the rating
     */
    @Transactional
    @RequestMapping(method = RequestMethod.POST)
    public EX add(@PathVariable Long id, @Valid @RequestBody RQ newRating) {
        JwtUser user = getUser();
        T target = findOne(targetRepository, id, name);
        X addedRating = addRating(user, target, newRating);
        return convertRating(addedRating);
    }

    private X addRating(JwtUser user, T target, RQ ratingRequest) {
        //Check if the user already has an existing rating
        X existingRating = findRatingByUser(getLatestRatings(target), user.getId());
        if (existingRating != null) {
            if (isRecent(existingRating)) {
                throw new ConflictException("Modify the old rating (ID: " + existingRating.getId() + ")");
            }

            existingRating.setLatest(false);
            ratingRepository.save(existingRating);
        }

        //Add the new rating
        X newRating = createNewRating(target, new User(user.getId()), ratingRequest);
        return ratingRepository.saveAndFlush(newRating);
    }

    /**
     * Update an existing rating.
     *
     * @param id        The target's ID
     * @param ratingId  The rating's ID
     * @param newRating The new rating
     *
     * @return the updated rating
     */
    @RequestMapping(value = "/{ratingId:\\d+}", method = RequestMethod.PUT)
    public EX edit(@PathVariable Long id, @PathVariable Long ratingId, @Valid @RequestBody RQ newRating) {
        JwtUser user = getUser();
        T target = findOne(targetRepository, id, name);

        //Find the rating
        X latestRating = findRatingByUser(getLatestRatings(target), user.getId());
        if (latestRating == null || !latestRating.getId().equals(ratingId) || !isRecent(latestRating)) {
            throw new ConflictException("Unable to find rating (either non existent, old or not yours)");
        }

        //Update the rating
        latestRating.setSeen(newRating.getSeen());
        latestRating.setRating(newRating.getRating());
        latestRating.setComment(newRating.getComment());
        X updatedRating = ratingRepository.saveAndFlush(latestRating);

        return convertRating(updatedRating);
    }

    protected boolean isRecent(X rating) {
        long minutesUntilNow = rating.getCreated().until(LocalDateTime.now(), ChronoUnit.MINUTES);
        return minutesUntilNow < RECENT_MINUTES;
    }

    /**
     * Convert a rating to it's extended JSON variant.
     *
     * @param rating The rating
     * @param <EX>   The rating return type
     *
     * @return the rating representation
     */
    protected abstract EX convertRating(X rating);

    //TODO
    protected abstract X createNewRating(T target, User user, RQ request);

    /**
     * Get latest ratings of a target.
     *
     * @param target The target
     *
     * @return the ratings
     */
    protected abstract List<X> getLatestRatings(T target);

    private X findRatingByUser(List<X> ratings, Long userId) {
        for (X rating : ratings) {
            if (rating.getUser().getId().equals(userId)) {
                return rating;
            }
        }
        return null;
    }

}
