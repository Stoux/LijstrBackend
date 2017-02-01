package nl.lijstr.services.migrate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import nl.lijstr.services.migrate.migrators.OldSiteMigrator;
import nl.lijstr.services.migrate.models.MigrationProgress;
import nl.lijstr.services.migrate.models.MigrationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Created by Stoux on 31-1-2017.
 */
@Service
public class MigrateService {

    @Autowired
    private MigratorFactory migratorFactory;

    private final Map<MigrationType, MigrationProgress> progressMap;

    public MigrateService() {
        this.progressMap = new ConcurrentHashMap<>();
    }

    /**
     * Migrate all movies from the old site.
     * If this returns false you can call {@link #getMovieProgress()}.
     *
     * @return started a NEW migration
     */
    public boolean migrateMovies() {
        return migrate(MigrationType.MOVIE);
    }

    /**
     * Get the current status of the movie migration.
     *
     * @return progress or null
     */
    public MigrationProgress getMovieProgress() {
        return getProgress(MigrationType.MOVIE);
    }

    /**
     * Clear the current progress for the movie migration.
     * This returns false when a migration is still in progress.
     *
     * @return is clear
     */
    public boolean clearMovieProgress() {
        return clearProgress(MigrationType.MOVIE);
    }

    private synchronized boolean migrate(MigrationType type) {
        MigrationProgress progress = getProgress(type);
        if (progress != null && !progress.isFinished()) {
            return false;
        }

        MigrationProgress newProgress = new MigrationProgress();
        OldSiteMigrator migrator = migratorFactory.createMigrator(type, newProgress);
        this.progressMap.put(type, newProgress);

        startMigration(migrator);
        return true;
    }

    private MigrationProgress getProgress(MigrationType type) {
        return this.progressMap.get(type);
    }

    private boolean clearProgress(MigrationType type) {
        MigrationProgress progress = getMovieProgress();
        if (progress == null || progress.isFinished()) {
            this.progressMap.remove(type);
            return true;
        } else {
            return false;
        }
    }

    @Async
    private void startMigration(OldSiteMigrator migrator) {
        migrator.migrate();
    }

}
