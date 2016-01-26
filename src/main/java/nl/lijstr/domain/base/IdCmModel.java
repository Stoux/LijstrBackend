package nl.lijstr.domain.base;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * An extended IdModel with created and last modified timestamps.
 */
@Getter
@Setter
@MappedSuperclass
public abstract class IdCmModel extends IdModel {

    @Column(name = "modified")
    private LocalDateTime lastModified;
    private LocalDateTime created;

}
