package nl.lijstr.common;

import nl.lijstr._TestUtils.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.math.BigInteger;

import static org.junit.Assert.*;

/**
 * Created by Leon Stam on 29-1-2016.
 */
public class ReflectUtilsTest {

    private Class<TestModel> mClass;

    private Container<Method> getter;
    private Container<Method> setter;

    @Before
    public void setUp() {
        mClass = TestModel.class;
        getter = new Container<>();
        setter = new Container<>();
    }

    @Test
    public void testFindBothFieldMethods() throws Exception {
        //Arrange
        String field = "varX";

        //Act
        boolean found = ReflectUtils.findFieldMethods(mClass, field, getter::setItem, setter::setItem);

        //Assert
        assertTrue(found);
        matchMethod("get", field, getter);
        matchMethod("set", field, setter, String.class);
    }

    @Test
    public void testFindGetMethod() throws Exception {
        //Arrange
        String getField = "varGet";

        //Act
        boolean foundGet = ReflectUtils.findFieldMethods(mClass, getField, getter::setItem, null);

        //Assert
        assertTrue(foundGet);
        matchMethod("is", getField, getter);
    }

    @Test
    public void testFindSetMethod() throws Exception {
        //Arrange
        String setField = "varSet";

        //Act
        boolean foundSet = ReflectUtils.findFieldMethods(mClass, setField, null, setter::setItem);

        //Assert
        assertTrue(foundSet);
        matchMethod("set", setField, setter, BigInteger.class);
    }

    @Test
    public void testMissingMethods() throws Exception {
        //Act
        boolean foundGet = ReflectUtils.findFieldMethods(mClass, "varSet", getter::setItem, null);
        boolean foundSet = ReflectUtils.findFieldMethods(mClass, "varGet", null, setter::setItem);

        //Assert
        assertFalse(foundGet);
        assertFalse(foundSet);
        assertFalse(getter.isPresent());
        assertFalse(setter.isPresent());
    }

    @Test
    public void coverConstructor() throws Exception {
        TestUtils.callPrivateConstructor(ReflectUtils.class);
    }

    private void matchMethod(String type, String field, Container<Method> container, Class<?>... params) {
        Method method = container.getItem();
        assertNotNull(method);

        //Check the name
        String combinedName = type + StringUtils.capitalize(field);
        assertEquals(combinedName, method.getName());

        //Check params
        assertEquals(params.length, method.getParameterCount());
        for (int i = 0; i < params.length; i++) {
            Class<?> foundClass = method.getParameterTypes()[i];
            Class<?> expectedClass = params[i];

            assertTrue(foundClass.isAssignableFrom(expectedClass));
        }
    }

    public class TestModel {
        @Getter
        @Setter
        private String varX;

        @Getter
        private boolean varGet;

        @Setter
        private BigInteger varSet;
    }
}