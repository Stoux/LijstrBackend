package nl.lijstr.configs;

/**
 * List of Retrofit Endpoints.
 * <p>
 * Only contains "public static final String" variables. These are all available endpoints.
 */
public final class RetrofitEndpoints {

    public static final String MY_API_FILMS = "http://api.myapifilms.com";
    public static final String OMDB = "http://www.omdbapi.com";
    public static final String MAILGUN = "https://api.mailgun.net";
    public static final String OLD_SITE = "https://stoux.nl";

    private RetrofitEndpoints() {
    }

}
