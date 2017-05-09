package nl.lijstr.api.movies.models.post;

import lombok.*;
import nl.lijstr.api.abs.base.models.post.PostedRequest;
import org.springframework.validation.annotation.Validated;

/**
 * Created by Leon Stam on 18-4-2016.
 */
@Getter
@Validated
@NoArgsConstructor
public class PostedMovieRatingRequest extends PostedRequest {

    private MovieRatingRequest ratingRequest;

    public PostedMovieRatingRequest(String imdbId, String youtubeId, MovieRatingRequest ratingRequest) {
        super(imdbId, youtubeId);
        this.ratingRequest = ratingRequest;
    }
}
