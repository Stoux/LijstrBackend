package nl.lijstr.processors.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This Annotation specifies that a Logger should be injected on the attached field.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectLogger {

    /**
     * The name of the logger that should be injected.
     *
     * @return the name
     */
    String value() default "";

}
