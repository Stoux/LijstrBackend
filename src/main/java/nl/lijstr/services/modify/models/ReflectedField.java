package nl.lijstr.services.modify.models;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * A container class that contains a Field and metadata.
 * <p>
 * This is used by {@link nl.lijstr.services.modify.ModelModifyService} to store
 * all related information for a certain field in.
 */
@Getter
@Setter
public class ReflectedField {

    private Field field;
    private Method getterMethod;
    private Method setterMethod;
    @Getter(AccessLevel.NONE)
    private boolean keepHistory;

    /**
     * Create a ReflectedField container.
     *
     * @param field the field that is being reflected
     */
    public ReflectedField(Field field) {
        this.field = field;
    }

    /**
     * Get the name of the field.
     *
     * @return the name
     */
    public String getFieldName() {
        return field.getName();
    }

    /**
     * Check if this field keeps history.
     *
     * @return keeps history
     */
    public boolean keepsHistory() {
        return keepHistory;
    }

}
