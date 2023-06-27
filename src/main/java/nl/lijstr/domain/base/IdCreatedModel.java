package nl.lijstr.domain.base;

import lombok.Getter;
import lombok.Setter;
import nl.lijstr.services.modify.annotations.NotModifiable;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;

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
