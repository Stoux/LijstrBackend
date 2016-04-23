package nl.lijstr.domain.other;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.*;
import nl.lijstr.domain.base.IdModel;

/**
 * A gif record.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MemeImage extends IdModel {

    @Column(nullable = false)
    private Integer imgWidth;

    @Column(nullable = false)
    private Integer imgHeight;

    @Column(nullable = false)
    private String imgSrc;

    private String imgSubtitle;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ApprovedFor approvedFor;

}
