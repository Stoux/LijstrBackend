package nl.lijstr.services.maf.handlers.util;

import java.lang.reflect.Method;
import lombok.*;
import nl.lijstr.exceptions.LijstrException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Leon Stam on 29-1-2016.
 */
public class FieldModifyHandlerTest {

    private FieldModifyHandler handler;

    @Before
    public void setUp() {
        handler = null;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public class TestModify {
        private String var;
        String nullVar;
        String varX;
        int varEqual;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public class OtherTestModify {
        public String var;
        String nullVar;
        String varY;
        int varEqual;
    }

    @Test
    public void testModify() throws Exception {
        //Arrange
        TestModify testModify = new TestModify("TEST", null, null, 10);
        OtherTestModify otherTestModify = new OtherTestModify(null, null, "TESTX", 10);
        handler = new FieldModifyHandler(testModify, otherTestModify);

        //Act
        handler.modify("var");
        handler.modify("nullVar");
        handler.modify("varEqual");

        //Act - 2 Params
        handler.modify("varX", "varY");

        //Assert
        assertNull(testModify.var);
        assertNull(testModify.nullVar);

        assertNotNull(testModify.varX);
        assertEquals(otherTestModify.varY, testModify.varX);
    }

    public class InvalidModel {
        private String var;
    }

    @Test(expected = LijstrException.class)
    public void testInvalidClass() throws Exception {
        //Arrange
        InvalidModel model = new InvalidModel();
        handler = new FieldModifyHandler(model, model);

        //Act
        handler.modify("var");

        //Assert
        fail();
    }

    @AllArgsConstructor
    public class DifferentModel {
        int varEqual;
    }

    @Test
    public void testCompareAndModify() throws Exception {
        //Arrange
        TestModify testModify = new TestModify("TEST", null, null, 10);
        DifferentModel differentModel = new DifferentModel(9001);
        handler = new FieldModifyHandler(testModify, differentModel);

        //Act
        handler.compareAndModify(
                testModify.var, differentModel.varEqual,
                integer -> "x" + integer, testModify::setVar
        );

        //Assert
        assertEquals("x9001", testModify.getVar());
    }

}