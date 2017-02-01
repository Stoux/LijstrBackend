package nl.lijstr.services.migrate;

import nl.lijstr.processors.annotations.InjectRetrofitService;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.services.maf.MafApiService;
import nl.lijstr.services.migrate.migrators.MovieMigrator;
import nl.lijstr.services.migrate.migrators.OldSiteMigrator;
import nl.lijstr.services.migrate.models.MigrationProgress;
import nl.lijstr.services.migrate.models.MigrationType;
import nl.lijstr.services.migrate.retrofit.OldSiteService;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Stoux on 1-2-2017.
 */
@Service
public class MigratorFactory {

    @InjectRetrofitService
    private OldSiteService oldSiteService;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MafApiService mafApiService;


    OldSiteMigrator createMigrator(final MigrationType type, final MigrationProgress progress) {
        switch (type) {
            case MOVIE:
                return new MovieMigrator(
                        oldSiteService, movieRepository, mafApiService, progress,
                        LogManager.getLogger(MovieMigrator.class)
                );

            case SERIES:
                throw new UnsupportedOperationException("Series has yet to be implemented.");
        }
        return null;
    }

}
