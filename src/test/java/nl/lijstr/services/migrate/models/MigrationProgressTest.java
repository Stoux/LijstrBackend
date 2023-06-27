package nl.lijstr.services.migrate.models;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static org.junit.Assert.*;

/**
 * Created by Stoux on 1-2-2017.
 */
public class MigrationProgressTest {

    private MigrationProgress progress;

    @Before
    public void setUp() throws Exception {
        progress = new MigrationProgress();
    }

    @Test
    public void intialStateCheck() throws Exception {
        //Assert
        assertFalse(progress.isFinished());
        assertFalse(progress.isFailed());
    }

    @Test
    public void finish() throws Exception {
        //Act
        progress.finish();

        //Assert
        assertTrue(progress.isFinished());
        assertFalse(progress.isFailed());
    }

    @Test
    public void fail() throws Exception {
        //Arrange
        Exception e = new Exception();

        //Act
        progress.fail(e);

        //Assert
        assertTrue(progress.isFinished());
        assertTrue(progress.isFailed());
        assertEquals(e, progress.getException());
    }

    @Test(expected = IllegalStateException.class)
    public void doubleFinish() throws Exception {
        //Act
        progress.finish();
        progress.fail(null);
    }

    @Test
    public void updated() throws Exception {
        mapCheck(progress::updated, progress::getUpdated);
    }

    @Test
    public void added() throws Exception {
        mapCheck(progress::added, progress::getAdded);
    }

    @Test
    public void filled() throws Exception {
        //Arrange
        assertEquals(0, progress.getAddedAndFilled().size());
        String id = "ID";
        String value = "VALUE";

        //Act
        progress.added(id, value);
        progress.filled(id);

        //Assert
        assertEquals(0, progress.getAdded().size());
        assertEquals(1, progress.getAddedAndFilled().size());
        assertTrue(progress.getAddedAndFilled().containsKey(id));
        assertEquals(value, progress.getAddedAndFilled().get(id));
    }

    @Test(expected = IllegalStateException.class)
    public void filledInvalidState() throws Exception {
        //Arrange
        String id = "ID";

        //Act
        progress.filled(id);

        //Assert
        fail();
    }

    private void mapCheck(BiConsumer<String, String> caller, Supplier<Map<String, String>> mapSupplier) {
        //Arrange
        assertEquals(0, mapSupplier.get().size());
        String id = "ID";
        String value = "VALUE";

        //Act
        caller.accept(id, value);

        //Assert
        Map<String, String> map = mapSupplier.get();
        assertEquals(1, map.size());
        assertTrue(map.containsKey(id));
        assertEquals(value, map.get(id));
    }

}