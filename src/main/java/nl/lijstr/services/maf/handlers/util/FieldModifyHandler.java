package nl.lijstr.services.maf.handlers.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import nl.lijstr.common.Container;
import nl.lijstr.common.ReflectUtils;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.exceptions.LijstrException;
import nl.lijstr.services.maf.models.ApiMovie;
import org.springframework.util.ReflectionUtils;

/**
 * Created by Leon Stam on 29-1-2016.
 */
@AllArgsConstructor
public class FieldModifyHandler {

    private Object o1;
    private Object o2;

    /**
     * Modify by fieldname.
     *
     * @param fieldName the field name in both Movie & ApiMovie
     */
    public void modify(String fieldName) {
        modify(fieldName, fieldName);
    }

    /**
     * Modify by fieldnames.
     *
     * @param fieldName    The Movie fieldname
     * @param apiFieldName The ApiMovie fieldname
     */
    public void modify(String fieldName, String apiFieldName) {
        Container<Method> movieFieldSetter = new Container<>();
        Object originalValue = getFieldValue(o1, fieldName, movieFieldSetter);
        Object newValue = getFieldValue(o2, apiFieldName, null);

        compareAndModify(originalValue, newValue, o -> o, object -> {
            Method method = movieFieldSetter.getItem();
            ReflectionUtils.invokeMethod(method, o1, object);
        });
    }

    /**
     * Compare and modify 2 values.
     *
     * @param originalValue     The original value
     * @param newValue          The new value
     * @param transformFunction A function to convert newValue to the same type as originalValue
     * @param setterFunction    A setter function to set the new value
     * @param <X>               The class of the original value
     * @param <Y>               The class of the new value
     */
    public <X, Y> void compareAndModify(X originalValue, Y newValue,
                                        Function<Y, X> transformFunction,
                                        Consumer<X> setterFunction) {
        //Get the modified value
        X modifiedValue = null;
        if (newValue != null) {
            modifiedValue = transformFunction.apply(newValue);
        }

        //Check if equal
        if (areEqual(originalValue, modifiedValue)) {
            return;
        }

        setterFunction.accept(modifiedValue);
    }

    private boolean areEqual(Object o1, Object o2) {
        if (o1 == null && o2 == null) {
            return true;
        }

        if (o1 == null || o2 == null) {
            return false;
        }

        return o1.equals(o2);
    }

    @SuppressWarnings("unchecked")
    private <X> X getFieldValue(Object onObject, String fieldName, Container<Method> setterContainer) {
        Class<?> aClass = onObject.getClass();
        Container<Method> methodContainer = new Container<>();
        Consumer<Method> setterConsumer = setterContainer == null ? null : setterContainer::setItem;
        if (!ReflectUtils.findFieldMethods(aClass, fieldName, methodContainer::setItem, setterConsumer)) {
            throw new LijstrException("Failed to find Getter for: " + aClass.getSimpleName() + " - " + fieldName);
        }

        try {
            return (X) methodContainer.getItem().invoke(onObject);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new LijstrException(e);
        }
    }

}
