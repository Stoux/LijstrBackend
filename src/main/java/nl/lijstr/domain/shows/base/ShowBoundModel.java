package nl.lijstr.domain.shows.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import lombok.*;
import nl.lijstr.domain.base.IdCmModel;
import nl.lijstr.domain.shows.Show;

/**
 * An entity that's connected to a show.
 */
@Getter
@Setter
@MappedSuperclass
public abstract class ShowBoundModel extends IdCmModel {

    @JsonIgnore
    @ManyToOne
    protected Show show;

}
