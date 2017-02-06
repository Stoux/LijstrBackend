package nl.lijstr.api.movies.models;

import java.time.LocalDateTime;
import lombok.Getter;
import nl.lijstr.domain.movies.MovieRequest;

/**
 * Summarized version of a {@link MovieRequest}.
 */
@Getter
public class MovieShortRequest {

    private long id;
    private long requestedBy;
    private LocalDateTime created;

    private String imdbId;
    private String youtubeUrl;

    private String title;
    private String year;
    private String imdbRating;

    private Long approvedBy;

    /**
     * Create a {@link MovieShortRequest} from a {@link MovieRequest}.
     *
     * @param request The request
     */
    public MovieShortRequest(MovieRequest request) {
        this.id = request.getId();
        this.requestedBy = request.getUser().getId();
        this.created = request.getCreated();

        this.imdbId = request.getImdbId();
        this.youtubeUrl = request.getYoutubeUrl();

        this.title = request.getTitle();
        this.year = request.getYear();
        this.imdbRating = request.getImdbRating();

        this.approvedBy = request.getApprovedBy() != null ? request.getApprovedBy().getId() : null;
    }

}
