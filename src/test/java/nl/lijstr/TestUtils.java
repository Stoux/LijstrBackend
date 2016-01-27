package nl.lijstr;

import java.lang.reflect.Field;
import nl.lijstr.common.Container;
import org.apache.logging.log4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.StringUtils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Common Test Utilities.
 */
public class TestUtils {

    /**
     * Mock a logger in a class.
     *
     * @param object    The bean/object with a logger field
     * @param fieldName The name of the logger field
     */
    public static void mockLogger(Object object, String fieldName) {
        Logger logger = mock(Logger.class);
        ReflectionTestUtils.setField(object, fieldName, logger);
    }

    /**
     * Mock a logger in a class.
     * This calls {@link #mockLogger(Object, String)} with "logger" as second param.
     *
     * @param object The bean/object with a logger field
     */
    public static void mockLogger(Object object) {
        mockLogger(object, "logger");
    }

    /**
     * Insert one or mocks into a class.
     * <p>
     * This will try to insert mock in the following order:
     * <ol>
     * <li>Field name: uncapitalized class name</li>
     * <li>Matching Mock type to Field type</li>
     * </ol>
     * <p>
     * NOTE: This method will call {@link org.junit.Assert#fail(String)} if it fails to insert the mock
     *
     * @param object The target
     * @param mocks  The mock objects that needs to inserted
     *
     * @throws Exception if literally anything goes wrong
     */
    public static void insertMocks(Object object, Object... mocks) throws Exception {
        for (Object mock : mocks) {
            if (!insertMockByFieldName(object, mock) && !insertMockByFieldType(object, mock)) {
                fail();
            }
        }
    }

    private static boolean insertMockByFieldName(Object object, Object mock) throws Exception {
        String mockedName = mock.getClass().getSimpleName();
        String[] splitMockedName = mockedName.split("\\$\\$");
        String fieldName = StringUtils.uncapitalize(splitMockedName[0]);

        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            ReflectionTestUtils.setField(object, field.getName(), mock);
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    private static boolean insertMockByFieldType(Object object, Object mock) throws Exception {
        Container<Field> fieldContainer = new Container<>();
        Class<?> mockClass = mock.getClass();

        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.getType().isAssignableFrom(mockClass)) {
                if (fieldContainer.isPresent()) {
                    fail("Found multiple fields for "
                            + mock.getClass().getSimpleName()
                            + " in "
                            + object.getClass().getSimpleName()
                            + ": "
                            + fieldContainer.getItem().getName() + " & " + field.getName()
                    );
                }

                fieldContainer.setItem(field);
            }
        }

        if (fieldContainer.isPresent()) {
            Field field = fieldContainer.getItem();
            ReflectionTestUtils.setField(object, field.getName(), mock);
            return true;
        } else {
            return false;
        }
    }

}
