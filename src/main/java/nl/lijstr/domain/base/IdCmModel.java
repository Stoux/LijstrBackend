package nl.lijstr.domain.base;

import java.time.LocalDateTime;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import nl.lijstr.services.modify.annotations.NotModifiable;

/**
 * An extended IdModel with created and last modified timestamps.
 */
@Getter
@Setter
@MappedSuperclass
public abstract class IdCmModel extends IdModel {

    @NotModifiable
    private LocalDateTime lastModified;

    @NotModifiable
    private LocalDateTime created;

    @PrePersist
    @PreUpdate
    private void fillTimes() {
        LocalDateTime now = LocalDateTime.now();
        if (created == null) {
            created = now;
        }
        lastModified = now;
    }

}
