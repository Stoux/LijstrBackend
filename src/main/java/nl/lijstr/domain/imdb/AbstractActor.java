package nl.lijstr.domain.imdb;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import lombok.*;
import nl.lijstr.domain.base.IdModel;
import nl.lijstr.domain.interfaces.PersonBound;

/**
 * Created by Stoux on 03/12/2015.
 */
@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractActor extends IdModel implements PersonBound {

    @JsonManagedReference
    @ManyToOne
    private Person person;

    @Column(length = 1000)
    private String character;
    private String characterUrl;
    private String photoUrl;

    private boolean mainCharacter;

    @Override
    public String getImdbId() {
        return person.getImdbId();
    }

}
