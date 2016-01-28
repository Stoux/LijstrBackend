package nl.lijstr.services.retrofit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that specifies the URL endpoint for a Retrofit service.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RetrofitServiceAnnotation {

    /**
     * The URL of the Endpoint.
     *
     * @return the endpoint
     */
    String value();

}
