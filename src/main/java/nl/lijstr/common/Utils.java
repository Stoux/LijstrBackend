package nl.lijstr.common;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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
     * Transform a Collection of items to a Map.
     *
     * @param items             The items
     * @param itemToKeyFunction Get the key from an item
     * @param <X>               The item class
     * @param <Y>               The key class
     *
     * @return the map
     */
    public static <X, Y> Map<Y, X> toMap(Collection<X> items, Function<X, Y> itemToKeyFunction) {
        return toMap(items, itemToKeyFunction, i -> i);
    }

    /**
     * Convert a List of items to a Map.
     *
     * @param items               The items
     * @param itemToKeyFunction   A function to get the key from an item
     * @param itemToValueFunction A function to get the value from an item
     * @param <X>                 The item class
     * @param <Y>                 The key class
     * @param <Z>                 The value class
     *
     * @return the map
     */
    public static <X, Y, Z> Map<Y, Z> toMap(Collection<X> items,
                                            Function<X, Y> itemToKeyFunction,
                                            Function<X, Z> itemToValueFunction) {
        final Map<Y, Z> map = new HashMap<>();
        items.forEach(i -> map.put(
                itemToKeyFunction.apply(i),
                itemToValueFunction.apply(i)
        ));
        return map;
    }

    /**
     * Update a List.
     * <p>
     * Especially useful for updating associated lists on an {@link javax.persistence.Entity}.
     *
     * @param currentItems        The current list
     * @param newItems            The new list
     * @param getAsStringFunction Get an item as {@link String}
     * @param getOrCreateFunction Get or creates a new item
     * @param <X>                 The class of the item
     */
    public static <X> void updateList(List<X> currentItems,
                                      Collection<String> newItems,
                                      Function<X, String> getAsStringFunction,
                                      Function<String, X> getOrCreateFunction) {
        final Map<String, X> itemMap = Utils.toMap(currentItems, getAsStringFunction);

        for (String newItem : newItems) {
            if (itemMap.containsKey(newItem)) {
                itemMap.remove(newItem);
            } else {
                X createdItem = getOrCreateFunction.apply(newItem);
                currentItems.add(createdItem);
            }
        }

        itemMap.values().forEach(currentItems::remove);
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
        X annotation = getAnnotation(field, annotationClass);
        return annotation != null;
    }

    /**
     * Get a field's Annotation.
     *
     * @param field           The field
     * @param annotationClass The class of the Annotation
     * @param <X>             The annotation
     *
     * @return the annotation or null
     */
    public static <X extends Annotation> X getAnnotation(Field field, Class<X> annotationClass) {
        return field.getDeclaredAnnotation(annotationClass);
    }

    /**
     * Get a class' annotation.
     *
     * @param aClass          The class
     * @param annotationClass The class of the annotation
     * @param <X>             The annotation
     *
     * @return the annotation or null
     */
    public static <X extends Annotation> X getAnnotation(Class<?> aClass, Class<X> annotationClass) {
        return aClass.getDeclaredAnnotation(annotationClass);
    }

    /**
     * Check if a Class has a certain annotation.
     *
     * @param clazz           The class
     * @param annotationClass The class of the annotation
     * @param <X>             The annotation
     *
     * @return has the annotation
     */
    public static <X extends Annotation> boolean hasAnnotation(Class<?> clazz, Class<X> annotationClass) {
        X annotation = clazz.getDeclaredAnnotation(annotationClass);
        return annotation != null;
    }

}
