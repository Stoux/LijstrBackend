package nl.lijstr.domain.movies;

import javax.persistence.Entity;
import lombok.*;
import nl.lijstr.domain.base.IdCmUserMovieModel;

/**
 * Created by Stoux on 03/12/2015.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MovieUserMeta extends IdCmUserMovieModel {

    private boolean wantToWatch;

}
