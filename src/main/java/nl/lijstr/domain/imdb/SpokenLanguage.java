package nl.lijstr.domain.imdb;

import javax.persistence.Column;
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
public class SpokenLanguage extends IdModel {

    @Column(unique = true, nullable = false)
    private String language;

}
