package nl.lijstr.domain.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.lijstr.domain.base.IdCreatedModel;
import nl.lijstr.services.modify.annotations.NotModifiable;

import jakarta.persistence.Entity;
import java.lang.reflect.Field;

/**
 * Created by Stoux on 26/01/2016.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@NotModifiable
public class FieldHistory extends IdCreatedModel {

    private String className;
    private Long objectId;
    private String field;
    private String oldValue;
    private String newValue;

    /**
     * Create a FieldHistory object.
     *
     * @param clazz    The class of the object
     * @param objectId The ID of the object
     * @param field    The field that's modified
     * @param oldValue The old value
     * @param newValue The new value
     *
     * @return The FieldHistory object
     */
    public static FieldHistory asFieldHistory(Class<?> clazz, long objectId, Field field, String oldValue, String newValue) {
        return new FieldHistory(
                getDatabaseClassName(clazz),
                objectId,
                field.getName(),
                oldValue,
                newValue
        );
    }

    /**
     * Get the name for the given class as it would be stored in the database.
     *
     * @param clazz The class
     *
     * @return the name
     */
    public static String getDatabaseClassName(Class<?> clazz) {
        return clazz.getSimpleName();
    }

}
