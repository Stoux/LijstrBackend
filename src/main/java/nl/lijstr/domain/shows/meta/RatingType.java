package nl.lijstr.domain.shows.meta;

/**
 * Describes the type of rating / status of the rating that is left on a {@link nl.lijstr.domain.shows.user.ShowRating}.
 */
public enum RatingType {

    /** A rating has been left for this show */
    RATED,
    /** A rating is calculated using the ratings of the seasons */
    CALCULATED,
    /** The user is not sure what to rate this show */
    UNKNOWN,
    /** The user hasn't left a rating yet */
    NONE,


}
