package nl.lijstr.common;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * Refleciton Utilities.
 */
public final class ReflectUtils {

    private static final String[] GETTER_PREFIXES = {"get", "is"};
    private static final String[] SETTER_PREFIXES = {"set"};

    private ReflectUtils() {
    }

    /**
     * Find the Setter & Getter methods for a field.
     * <p>
     * You can enter null for a consumer to skip that type.
     *
     * @param clazz          The class
     * @param fieldName      The field
     * @param getterConsumer The consumer for the Getter method or null
     * @param setterConsumer The consumer for the Setter method or null
     *
     * @return found the methods
     */
    public static boolean findFieldMethods(final Class<?> clazz,
                                           String fieldName,
                                           Consumer<Method> getterConsumer,
                                           Consumer<Method> setterConsumer) {
        final String capitalizedField = StringUtils.capitalize(fieldName);

        final Container<Boolean> foundGetter = new Container<>(false);
        final Container<Boolean> foundSetter = new Container<>(false);

        //Loop through methods. Try to match as Getter or Setter
        ReflectionUtils.MethodCallback callback = method -> {
            handleMethod(method, GETTER_PREFIXES, capitalizedField, getterConsumer, foundGetter);
            handleMethod(method, SETTER_PREFIXES, capitalizedField, setterConsumer, foundSetter);
        };

        //Filter the methods to end with name of the field.
        ReflectionUtils.MethodFilter filter = method ->
                method.getName().endsWith(capitalizedField) && Modifier.isPublic(method.getModifiers());

        //Execute
        ReflectionUtils.doWithMethods(clazz, callback, filter);
        return foundGetter.getItem() && foundSetter.getItem();
    }

    private static void handleMethod(Method method, String[] prefixes,
                                     String field, Consumer<Method> methodConsumer,
                                     Container<Boolean> foundContainer) {
        if (methodConsumer == null) {
            return;
        }

        if (matchMethod(prefixes, field, method, methodConsumer)) {
            foundContainer.setItem(true);
        }
    }

    private static boolean matchMethod(String[] prefixes, String capitalizedField, Method method, Consumer<Method> setter) {
        for (String prefix : prefixes) {
            if (method.getName().equals(prefix + capitalizedField)) {
                setter.accept(method);
                return true;
            }
        }
        return false;
    }


}
