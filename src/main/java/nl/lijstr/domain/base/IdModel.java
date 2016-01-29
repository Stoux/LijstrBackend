package nl.lijstr.domain.base;

import javax.persistence.*;
import lombok.*;
import nl.lijstr.services.modify.annotations.NotModifiable;

/**
 * A basic JPA Entity that has a auto-generated id.
 */
@Getter
@Setter
@MappedSuperclass
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class IdModel {

    @Id
    @GeneratedValue
    @NotModifiable
    protected Long id;

}
