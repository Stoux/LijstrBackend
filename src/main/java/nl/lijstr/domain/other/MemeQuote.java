package nl.lijstr.domain.other;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.*;
import nl.lijstr.domain.base.IdModel;

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
