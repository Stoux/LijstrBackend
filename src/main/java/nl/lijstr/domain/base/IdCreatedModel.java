package nl.lijstr.domain.base;

import java.time.LocalDateTime;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import lombok.*;
import nl.lijstr.services.modify.annotations.NotModifiable;

/**
 * An extended IdModel with created timestamp.
 */
@Getter
@Setter
@MappedSuperclass
public abstract class IdCreatedModel extends IdModel {

    @NotModifiable
    private LocalDateTime created;

    @SuppressWarnings("squid:UnusedPrivateMethod")
    @PrePersist
    @PreUpdate
    private void fillTimes() {
        if (created == null) {
            created = LocalDateTime.now();
        }
    }

}
