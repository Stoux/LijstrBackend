package nl.lijstr.api.movies.models.post;

import lombok.*;
import nl.lijstr.api.abs.base.models.post.PostedRequest;
import nl.lijstr.api.abs.base.models.post.RatingRequest;
import org.springframework.validation.annotation.Validated;

/**
 * Created by Leon Stam on 18-4-2016.
 */
@Getter
@Validated
@NoArgsConstructor
public class PostedMovieRatingRequest extends PostedRequest {

    private RatingRequest ratingRequest;

    public PostedMovieRatingRequest(String imdbId, String youtubeId, RatingRequest ratingRequest) {
        super(imdbId, youtubeId);
        this.ratingRequest = ratingRequest;
    }
}
