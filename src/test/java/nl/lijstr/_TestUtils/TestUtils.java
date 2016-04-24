package nl.lijstr._TestUtils;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import nl.lijstr.common.Container;
import nl.lijstr.common.Utils;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.apache.logging.log4j.Logger;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import retrofit2.Call;
import retrofit2.Response;

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
     *
     * @return the mocked logger
     */
    public static Logger mockLogger(Object object, String fieldName) {
        Logger logger = mock(Logger.class);
        ReflectionTestUtils.setField(object, fieldName, logger);
        return logger;
    }

    /**
     * Mock a logger in a class.
     * This calls {@link #mockLogger(Object, String)} with "logger" as second param.
     *
     * @param object The bean/object with a logger field
     *
     * @return the mocked logger
     */
    public static Logger mockLogger(Object object) {
        return mockLogger(object, "logger");
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

        Class targetClass = object.getClass();
        while(targetClass != null) {
            try {
                Field field = targetClass.getDeclaredField(fieldName);
                ReflectionTestUtils.setField(object, field.getName(), mock);
                return true;
            } catch (NoSuchFieldException e) {
                //Ignore this
            }

            targetClass = targetClass.getSuperclass();
        }
        return false;
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

    /**
     * Get a given argument of an invocation.
     *
     * @param invocation The invocation
     * @param paramIndex The index of the param (zero-based)
     * @param <T>        The class of the resulting object
     *
     * @return the object
     */
    @SuppressWarnings("unchecked")
    public static <T> T getInvocationParam(InvocationOnMock invocation, int paramIndex) {
        return (T) invocation.getArguments()[paramIndex];
    }

    /**
     * Get a Field's value.
     *
     * @param object    The object
     * @param fieldName The field
     * @param <X>       Casted to
     *
     * @return the value
     * @throws IllegalAccessException if no access
     */
    @SuppressWarnings("unchecked")
    public static <X> X getFieldValue(Object object, String fieldName) throws Exception {
        return (X) ReflectionTestUtils.getField(object, fieldName);
    }

    /**
     * Call a Private constructor for coverage.
     *
     * @param forClass The class it's going to be called on
     * @param <X>      The class
     *
     * @throws Exception if anything fails
     */
    public static <X> void callPrivateConstructor(Class<X> forClass) throws Exception {
        Constructor<X> constructor = forClass.getDeclaredConstructor();
        ReflectionUtils.makeAccessible(constructor);

        X instance = constructor.newInstance();
        assertNotNull(instance);
    }

    /**
     * Get a test resource.
     *
     * @param name The name (and path) of the file
     *
     * @return the inputstream
     */
    public static InputStream getTestResource(String name) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
    }

    /**
     * Create a mocked success call.
     *
     * @param returnObject The object that should be returned from the call
     * @param <X>          The object class
     *
     * @return the call
     * @throws IOException Retrofit exception
     */
    public static <X> Call<X> successCall(X returnObject) throws IOException {
        return createCall(Response.success(returnObject));
    }

    /**
     * Create a mocked failing call.
     *
     * @param errorCode The returned errorCode
     * @param message   The error message
     * @param <X>       The call's model
     *
     * @return the call
     * @throws IOException Retrofit exception
     */
    public static <X> Call<X> failedCall(int errorCode, String message) throws IOException {
        return createCall(
                Response.error(errorCode, ResponseBody.create(
                        MediaType.parse("application/json"),
                        new Gson().toJson(Utils.asMap("error", message))
                ))
        );
    }

    private static <X> Call<X> createCall(Response<X> mockedResponse) throws IOException {
        Call<X> mockedCall = mock(Call.class);
        doReturn(mockedResponse).when(mockedCall).execute();
        return mockedCall;
    }

}
