package nl.lijstr.domain.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.lijstr.domain.base.IdModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

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
