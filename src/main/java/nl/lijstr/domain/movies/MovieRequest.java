package nl.lijstr.domain.movies;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.*;
import nl.lijstr.domain.base.IdCmUserModel;
import nl.lijstr.domain.users.User;

/**
 * Created by Stoux on 03/12/2015.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class MovieRequest extends IdCmUserModel {

    @Column(nullable = false)
    private String imdbId;
    private String youtubeUrl;

    private String title;
    private String year;
    private String imdbRating;

    @ManyToOne
    private User approvedBy;
    private LocalDateTime addedOn;

    /**
     * Create a new MovieRequest.
     *
     * @param imdbId     The IMDB ID
     * @param youtubeUrl An optional YouTube URL
     * @param title      The title
     * @param year       The release year
     * @param imdbRating The rating
     */
    public MovieRequest(String imdbId, String youtubeUrl, String title, String year, String imdbRating) {
        this.imdbId = imdbId;
        this.youtubeUrl = youtubeUrl;
        this.title = title;
        this.year = year;
        this.imdbRating = imdbRating;
    }
}
