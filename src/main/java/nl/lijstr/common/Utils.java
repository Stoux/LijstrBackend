package nl.lijstr.common;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Common General Utilities.
 */
public final class Utils {

    private Utils() {
    }

    /**
     * Quick and Dirty way to go from Array to Map.
     * Maps every 2 values in the array as Key => Value.
     *
     * @param objects The objects (need to x * 2)
     *
     * @return the map
     */
    public static Map<Object, Object> asMap(Object... objects) {
        assert objects.length % 2 == 0;
        Map<Object, Object> map = new HashMap<>();
        for (int i = 0; i < objects.length; i += 2) {
            map.put(objects[i], objects[i + 1]);
        }
        return map;
    }

    /**
     * Check if a Field has one (or more) of the annotations.
     *
     * @param field             The field
     * @param annotationClasses The annotation classes
     *
     * @return has annotations
     */
    @SafeVarargs
    public static boolean hasOneofAnnotations(Field field, Class<? extends Annotation>... annotationClasses) {
        for (Class<? extends Annotation> annotationClass : annotationClasses) {
            if (hasAnnotation(field, annotationClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a Field has all the given annotations.
     *
     * @param field             The field
     * @param annotationClasses The annotation classes
     *
     * @return has annotations
     */
    @SafeVarargs
    public static boolean hasAllAnnotations(Field field, Class<? extends Annotation>... annotationClasses) {
        for (Class<? extends Annotation> annotationClass : annotationClasses) {
            if (!hasAnnotation(field, annotationClass)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if a Field has a certain annotation.
     *
     * @param field           The field
     * @param annotationClass The class of the Annotation
     * @param <X>             The Annotation
     *
     * @return has the annotation
     */
    public static <X extends Annotation> boolean hasAnnotation(Field field, Class<X> annotationClass) {
        X annotation = field.getDeclaredAnnotation(annotationClass);
        return annotation != null;
    }

}
