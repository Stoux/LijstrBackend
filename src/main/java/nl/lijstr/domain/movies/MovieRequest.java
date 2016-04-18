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

    @ManyToOne
    private User approvedBy;
    private LocalDateTime addedOn;

    /**
     * Create a new MovieRequest.
     *
     * @param imdbId     The IMDB ID
     * @param youtubeUrl An optional YouTube URL
     */
    public MovieRequest(String imdbId, String youtubeUrl) {
        this.imdbId = imdbId;
        this.youtubeUrl = youtubeUrl;
    }
}
