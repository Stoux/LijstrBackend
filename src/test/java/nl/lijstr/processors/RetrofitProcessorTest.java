package nl.lijstr.processors;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import nl.lijstr.processors.annotations.InjectRetrofitService;
import nl.lijstr.services.retrofit.RetrofitService;
import nl.lijstr.services.retrofit.annotations.RetrofitServiceAnnotation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;

import static nl.lijstr._TestUtils.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Stoux on 28/01/2016.
 */
public class RetrofitProcessorTest {

    private RetrofitProcessor retrofitProcessor;
    private RetrofitService mockedRetrofitService;

    private List<ClassInstanceContainer> createdServices;

    @Before
    public void setUp() throws Exception {
        createdServices = new ArrayList<>();

        mockedRetrofitService = mock(RetrofitService.class);
        when(mockedRetrofitService.createRetrofitService(anyString(), any())).thenAnswer(this::createRetrofitService);

        retrofitProcessor = new RetrofitProcessor();
        insertMocks(retrofitProcessor, mockedRetrofitService);
    }

    //Creates a Retrofit Mock as answer.
    private Object createRetrofitService(InvocationOnMock invocation) {
        Class<?> aClass = getInvocationParam(invocation, 1);
        Object aClassInstance = mock(aClass);
        createdServices.add(new ClassInstanceContainer(aClass, aClassInstance));
        return aClassInstance;
    }


    @Test
    public void testValidBean() {
        //Arrange
        class ValidBean {
            @InjectRetrofitService
            private TestRetrofitService service;
            private String randomVar;
        }
        ValidBean validBean = new ValidBean();

        //Act
        executeAndConfirmSameBean(validBean, 1);

        //Assert
        assertNotNull(validBean.service);
        assertNull(validBean.randomVar);
        sameService(validBean.service, 0);
    }

    @Test
    public void testMultipleValidBean() {
        //Arrange
        class DoubleValidBean {
            @InjectRetrofitService
            private TestRetrofitService service;
            @InjectRetrofitService
            private OtherTestRetrofitService otherService;
        }
        DoubleValidBean validBean = new DoubleValidBean();

        //Act
        executeAndConfirmSameBean(validBean, 2);

        //Arrange
        sameService(validBean.service, 0);
        sameService(validBean.otherService, 1);
    }

    @Test
    public void testInvalidBean() {
        //Arrange
        class InvalidBean {
            @InjectRetrofitService
            private InvalidRetrofitService invalidSerivce;
        }
        InvalidBean invalidBean = new InvalidBean();

        //Act
        executeAndConfirmSameBean(invalidBean, 0);

        //Arrange
        assertNull(invalidBean.invalidSerivce);
    }


    private void sameService(Object inBean, int serviceListIndex) {
        assertNotNull(inBean);
        Object createdService = createdServices.get(serviceListIndex).instance;
        assertEquals(createdService, inBean);
    }

    private void executeAndConfirmSameBean(Object bean, int expectedListSize) {
        //Act
        Object resultingBean = retrofitProcessor.postProcessBeforeInitialization(bean, bean.getClass().getSimpleName());

        //Assert
        assertEquals(bean, resultingBean);
        assertEquals(expectedListSize, createdServices.size());
        verify(mockedRetrofitService, times(expectedListSize)).createRetrofitService(anyString(), any());
    }


    @RetrofitServiceAnnotation("TEST_ENDPOINT")
    public interface TestRetrofitService {

    }

    @RetrofitServiceAnnotation("OTHER_ENDPOINT")
    public interface OtherTestRetrofitService {

    }

    public interface InvalidRetrofitService {

    }

    @AllArgsConstructor
    private class ClassInstanceContainer {
        private Class<?> aClass;
        private Object instance;
    }

}