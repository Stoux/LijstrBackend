package nl.lijstr.services.migrate.migrators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interface for migrator classes.
 */
public interface OldSiteMigrator {

    /**
     * Pattern that matches the IMDB links on the old site.
     */
    Pattern IMDB_PATTERN = Pattern.compile("^https?:\\/\\/www\\.imdb\\.com\\/title\\/(tt\\d+)\\/$");

    /**
     * Start the migration.
     */
    void migrate();

    /**
     * Get an IMDB ID from the old site IMDB link.
     * TODO: Add tests
     *
     * @param imdbLink The IMDB link
     *
     * @return the ID
     */
    default String getImdbId(String imdbLink) {
        if (imdbLink != null) {
            Matcher matcher = IMDB_PATTERN.matcher(imdbLink);
            if (matcher.matches()) {
                return matcher.group(1);
            }
        }
        throw new IllegalArgumentException(
                "Invalid IMDB link: " + imdbLink
        );
    }

}
