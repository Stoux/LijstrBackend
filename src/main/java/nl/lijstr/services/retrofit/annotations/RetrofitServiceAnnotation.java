package nl.lijstr.services.retrofit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import okhttp3.Interceptor;

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

    /**
     * An optional class that can be added as interceptor on the HttpClient under Retrofit.
     *
     * @return the class
     */
    Class<? extends Interceptor> interceptorClass() default Interceptor.class;

    /**
     * Inject the interceptor with Spring autowiring.
     *
     * @return should inject
     */
    boolean springInjectInterceptor() default false;

    /**
     * The timeout for connecting to an endpoint.
     *
     * @return timeout in seconds
     */
    int connectTimeout() default 10;

    /**
     * The timeout for reading the response from an endpoint.
     *
     * @return timeout in seconds
     */
    int readTimeout() default 10;

    /**
     * The timeout for writing data to an endpoint.
     *
     * @return timeout in seconds
     */
    int writeTimeout() default 10;

}
