package nl.lijstr.domain.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nl.lijstr.domain.base.IdModel;
import nl.lijstr.services.modify.annotations.NotModifiable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

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
