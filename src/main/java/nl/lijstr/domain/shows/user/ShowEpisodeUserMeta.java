package nl.lijstr.domain.shows.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import nl.lijstr.domain.base.IdCmUserModel;
import nl.lijstr.domain.shows.ShowEpisode;
import nl.lijstr.domain.users.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Meta data of a {@link nl.lijstr.domain.users.User} about a {@link ShowEpisode}.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = { "user_id", "episode_id" })
})
@JsonIgnoreProperties({"id"})
public class ShowEpisodeUserMeta extends IdCmUserModel {

    @JsonBackReference
    @ManyToOne
    private ShowEpisode episode;

    @Setter(AccessLevel.PRIVATE)
    @Column(nullable = false)
    private boolean seen;

    @Column
    private LocalDateTime seenOn;

    @Column
    private String reaction;

    public ShowEpisodeUserMeta(User user, ShowEpisode episode, boolean seen, LocalDateTime seenOn, String reaction) {
        this.user = user;
        this.episode = episode;
        this.seen = seen;
        this.seenOn = seenOn;
        this.reaction = reaction;
    }

    @JsonProperty("user")
    public Long getUserId() {
        return this.getUser() != null ? this.getUser().getId() : null;
    }

    public void updateSeen(boolean seen) {
        if (!this.isSeen() && seen) {
            // Seen for the first time
            setSeenOn(LocalDateTime.now());
        } else if (isSeen() && !seen) {
            // Marked as not seen
            setSeenOn(null);
        }

        setSeen(seen);
    }


}
