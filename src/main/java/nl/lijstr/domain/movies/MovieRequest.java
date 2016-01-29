package nl.lijstr.domain.movies;

import javax.persistence.Column;
import javax.persistence.Entity;
import lombok.*;
import nl.lijstr.domain.base.IdCmUserModel;

/**
 * Created by Stoux on 03/12/2015.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MovieRequest extends IdCmUserModel {

    @Column(nullable = false)
    private String imdbId;
    private String youtubeUrl;

}
