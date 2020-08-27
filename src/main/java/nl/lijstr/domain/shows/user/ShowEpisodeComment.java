package nl.lijstr.domain.shows.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.lijstr.domain.base.IdCmUserModel;
import nl.lijstr.domain.shows.ShowEpisode;
import nl.lijstr.domain.users.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * A {@link nl.lijstr.domain.users.User}'s comment on a {@link ShowEpisode}.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ShowEpisodeComment extends IdCmUserModel {

    @JsonBackReference
    @ManyToOne
    private ShowEpisode episode;

    @Column(length = 56000, nullable = false)
    private String comment;

    @Column(nullable = false)
    private boolean spoilers;

    public ShowEpisodeComment(User user, ShowEpisode episode, String comment, boolean spoilers) {
        this.user = user;
        this.episode = episode;
        this.comment = comment;
        this.spoilers = spoilers;
    }

    @JsonProperty("user")
    public Long getUserId() {
        return this.getUser() != null ? this.getUser().getId() : null;
    }

}
