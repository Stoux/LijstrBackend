package nl.lijstr.domain.other;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import lombok.*;
import nl.lijstr.domain.base.IdModel;
import nl.lijstr.services.modify.annotations.NotModifiable;

/**
 * A suggestion for a override of a FieldHistory entry.
 */
@Getter
@AllArgsConstructor
@Entity
@NotModifiable
public class FieldHistorySuggestion extends IdModel {

    @OneToOne(cascade = CascadeType.REMOVE)
    private FieldHistory overwriteTarget;
    private String suggestedChange;

}
