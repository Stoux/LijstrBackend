package nl.lijstr.services.retrofit.annotations;

/**
 * An annotation that specifies the URL endpoint for a Retrofit service.
 */
public @interface RetrofitServiceAnnotation {

    /**
     * The URL of the Endpoint.
     *
     * @return the endpoint
     */
    String value();

}
