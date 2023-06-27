package nl.lijstr.domain.base;

import lombok.Getter;
import lombok.Setter;
import nl.lijstr.services.modify.annotations.NotModifiable;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

/**
 * A basic JPA Entity that has a auto-generated id.
 */
@Getter
@Setter
@MappedSuperclass
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class IdModel {

    @Id
    @GeneratedValue(
            strategy = GenerationType.AUTO,
            generator = "native"
    )
    @GenericGenerator(
            name = "native"
    )
    @NotModifiable
    protected Long id;

}
