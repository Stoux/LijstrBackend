package nl.lijstr.domain.base;

import java.time.LocalDateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static nl.lijstr._TestUtils.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Stoux on 29/01/2016.
 */
public class IdCmModelTest {

    @Test
    public void createdFillTimes() {
        //Arrange
        TestModel testModel = new TestModel();

        //Pre-act asserts
        assertNull(testModel.getCreated());
        assertNull(testModel.getLastModified());

        //Act
        ReflectionTestUtils.invokeMethod(testModel, "fillTimes");
        changeTime();

        //Assert
        assertNotNull(testModel.getCreated());
        assertEquals(testModel.getCreated(), testModel.getLastModified());
        assertTrue(testModel.getCreated().isAfter(LocalDateTime.now().minusMinutes(1)));
        assertTrue(testModel.getCreated().isBefore(LocalDateTime.now()));
    }

    @Test
    public void modifyFillTimes() {
        //Arrange
        TestModel testModel = new TestModel();
        final LocalDateTime dateTime = LocalDateTime.now().minusSeconds(5);
        testModel.setCreated(dateTime);
        testModel.setLastModified(dateTime);

        //Act
        ReflectionTestUtils.invokeMethod(testModel, "fillTimes");
        changeTime();

        //Assert
        assertNotNull(testModel.getCreated());
        assertNotNull(testModel.getLastModified());
        assertNotEquals(testModel.getCreated(), testModel.getLastModified());
        assertTrue(testModel.getCreated().isBefore(testModel.getLastModified()));
        assertTrue(testModel.getLastModified().isBefore(LocalDateTime.now()));
    }

    private void changeTime() {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            fail("Failed to sleep...");
        }
    }

    private class TestModel extends IdCmModel {

    }

}