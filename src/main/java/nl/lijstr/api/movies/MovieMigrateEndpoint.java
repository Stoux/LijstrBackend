package nl.lijstr.api.movies;

import java.util.Map;
import nl.lijstr.api.abs.AbsService;
import nl.lijstr.common.Utils;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.services.migrate.MigrateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint for movie migrations from the old site.
 */
@Secured(Permission.ADMIN)
@RestController
@RequestMapping(value = "/movies/migrate", produces = "application/json")
public class MovieMigrateEndpoint extends AbsService {

    @Autowired
    private MigrateService migrateService;

    /**
     * Migrate movies from the old site.
     *
     * @return whether or not a new migration has started
     */
    @RequestMapping(method = RequestMethod.POST)
    public Map migrate() {
        boolean newMigration = this.migrateService.migrateMovies();
        return Utils.asMap("newMigration", newMigration);
    }

    /**
     * Get the current state of a movie migration.
     *
     * @return the progress
     */
    @RequestMapping(method = RequestMethod.GET)
    public Map status() {
        return Utils.asMap("progress", this.migrateService.getMovieProgress());
    }

    /**
     * Clear the migration progress of a finished migration.
     * Returns false when a migration is not finished yet.
     *
     * @return is clear
     */
    @RequestMapping(method = RequestMethod.DELETE)
    public Map clearFinishedMigration() {
        return Utils.asMap("cleared", this.migrateService.clearMovieProgress());
    }

}
