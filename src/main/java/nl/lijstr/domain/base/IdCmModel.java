package nl.lijstr.domain.base;

import lombok.Getter;
import lombok.Setter;
import nl.lijstr.services.modify.annotations.NotModifiable;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;

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

    @SuppressWarnings("squid:UnusedPrivateMethod")
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
