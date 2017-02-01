package nl.lijstr.services.migrate;

import nl.lijstr.services.migrate.migrators.MovieMigrator;
import nl.lijstr.services.migrate.migrators.OldSiteMigrator;
import nl.lijstr.services.migrate.models.MigrationProgress;
import nl.lijstr.services.migrate.models.MigrationType;
import org.junit.Before;
import org.junit.Test;

import static nl.lijstr._TestUtils.TestUtils.*;
import static org.junit.Assert.*;

/**
 * Created by Stoux on 1-2-2017.
 */
public class MigratorFactoryTest {

    private MigratorFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new MigratorFactory();
    }

    @Test
    public void createMovieMigrator() throws Exception {
        //Arrange
        MigrationProgress progress = new MigrationProgress();

        //Act
        OldSiteMigrator migrator = factory.createMigrator(MigrationType.MOVIE, progress);

        //Assert
        assertNotNull(migrator);
        assertEquals(MovieMigrator.class, migrator.getClass());
        assertEquals(progress, getFieldValue(migrator, "currentProgress"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void createSeriesMigrator() throws Exception {
        //Act
        factory.createMigrator(MigrationType.SERIES, null);

        //Assert
        fail();
    }
}