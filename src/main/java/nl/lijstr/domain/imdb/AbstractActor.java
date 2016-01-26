package nl.lijstr.domain.imdb;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import nl.lijstr.domain.base.IdModel;

/**
 * Created by Stoux on 03/12/2015.
 */
@Getter
@Setter
@MappedSuperclass
public abstract class AbstractActor extends IdModel {

    @ManyToOne
    private Person person;

    private String character;
    private String characterUrl;
    private String photoUrl;

    private boolean mainCharacter;

}
