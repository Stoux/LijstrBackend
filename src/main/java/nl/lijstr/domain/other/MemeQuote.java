package nl.lijstr.domain.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.lijstr.domain.base.IdModel;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 * Created by Stoux on 23/04/2016.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MemeQuote extends IdModel {

    private String quote;

    @Enumerated(EnumType.ORDINAL)
    private ApprovedFor approvedFor;

}
