package nl.lijstr.api.movies.models.post;

import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;

/**
 * Created by Leon Stam on 18-4-2016.
 */
@Getter
@Validated
@NoArgsConstructor
public class PostedMovieRatingRequest extends PostedMovieRequest {

    private MovieRatingRequest ratingRequest;

    public PostedMovieRatingRequest(String imdbId, String youtubeId, MovieRatingRequest ratingRequest) {
        super(imdbId, youtubeId);
        this.ratingRequest = ratingRequest;
    }
}
