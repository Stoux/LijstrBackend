package nl.lijstr.domain.imdb;

import javax.persistence.Entity;
import lombok.*;
import nl.lijstr.domain.base.IdModel;

/**
 * Created by Stoux on 03/12/2015.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Person extends IdModel {

    private String imdbId;
    private String name;

}
