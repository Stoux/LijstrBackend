package nl.lijstr.domain.shows.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.lijstr.domain.base.IdCmUserModel;
import nl.lijstr.domain.shows.ShowSeason;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * A {@link nl.lijstr.domain.users.User}'s comment on a {@link ShowSeason}.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ShowSeasonComment extends IdCmUserModel {

    @JsonBackReference
    @ManyToOne
    private ShowSeason season;

    @Column(length = 5000, nullable = false)
    private String comment;

    @Column(nullable = false)
    private boolean spoilers;

}
