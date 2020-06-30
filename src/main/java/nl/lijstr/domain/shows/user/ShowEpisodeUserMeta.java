package nl.lijstr.domain.shows.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.lijstr.domain.base.IdCmUserModel;
import nl.lijstr.domain.shows.ShowEpisode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

/**
 * Meta data of a {@link nl.lijstr.domain.users.User} about a {@link ShowEpisode}.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ShowEpisodeUserMeta extends IdCmUserModel {

    @JsonBackReference
    @ManyToOne
    private ShowEpisode episode;

    @Column(nullable = false)
    private boolean seen;

    @Column
    private LocalDateTime seenOn;

    @Column
    private String reaction;

}
