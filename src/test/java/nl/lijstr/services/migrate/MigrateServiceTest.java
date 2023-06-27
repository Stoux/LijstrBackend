package nl.lijstr.services.migrate;

import nl.lijstr.common.Container;
import nl.lijstr.services.migrate.migrators.OldSiteMigrator;
import nl.lijstr.services.migrate.models.MigrationProgress;
import nl.lijstr.services.migrate.models.MigrationType;
import org.junit.Before;
import org.junit.Test;

import static nl.lijstr._TestUtils.TestUtils.getInvocationParam;
import static nl.lijstr._TestUtils.TestUtils.insertMocks;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Stoux on 1-2-2017.
 */
public class MigrateServiceTest {

    //Subject
    private MigrateService migrateService;

    //Used services
    private OldSiteMigrator migrator;

    private Container<MigrationType> typeContainer;
    private Container<MigrationProgress> progressContainer;

    @Before
    public void setUp() throws Exception {
        migrateService = new MigrateService();
        typeContainer = new Container<>();
        progressContainer = new Container<>();

        //Inject mocks
        migrator = mock(OldSiteMigrator.class);
        MigratorFactory migratorFactory = mock(MigratorFactory.class);
        insertMocks(migrateService, migratorFactory);

        //Catch the passed arguments to the factory create method
        when(migratorFactory.createMigrator(any(), any())).thenAnswer(invocation -> {
            typeContainer.setItem(getInvocationParam(invocation, 0));
            progressContainer.setItem(getInvocationParam(invocation, 1));
            return migrator;
        });
    }

    @Test
    public void notStarted() throws Exception {
        //Assert
        assertNull(migrateService.getMovieProgress());
    }

    @Test
    public void startMigration() throws Exception {
        //Act
        boolean started = migrateService.migrateMovies();
        MigrationProgress progress = migrateService.getMovieProgress();

        //Assert
        assertTrue(started);
        assertNotNull(progress);

        assertEquals(progress, progressContainer.getItem());
        assertEquals(MigrationType.MOVIE, typeContainer.getItem());

        verify(migrator, times(1)).migrate();
    }

    @Test
    public void startMigrationTwice() throws Exception {
        //Act
        boolean startedFirst = migrateService.migrateMovies();
        MigrationProgress progressFirst = migrateService.getMovieProgress();

        boolean startedSecond = migrateService.migrateMovies();
        MigrationProgress progressSecond = migrateService.getMovieProgress();

        //Assert
        assertTrue(startedFirst);
        assertFalse(startedSecond);
        assertEquals(progressFirst, progressSecond);

        verify(migrator, times(1)).migrate();
    }
}