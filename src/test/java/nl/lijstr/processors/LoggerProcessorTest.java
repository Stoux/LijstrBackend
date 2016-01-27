package nl.lijstr.processors;

import nl.lijstr.processors.annotations.InjectLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Stoux on 27/01/2016.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(LogManager.class)
public class LoggerProcessorTest {

    private LoggerProcessor loggerProcessor;

    @Before
    public void setUp() {
        loggerProcessor = new LoggerProcessor();

        //Setup the LogManager to return a mocked logger using the passed param.
        PowerMockito.mockStatic(LogManager.class);
        when(LogManager.getLogger(anyString())).thenAnswer(invocation -> {
            //Get passed argument
            final String loggerName = (String) invocation.getArguments()[0];

            //Mock the logger
            Logger mockLogger = mock(Logger.class);
            when(mockLogger.getName()).thenReturn(loggerName);

            return mockLogger;
        });
    }


    @Test
    public void testValidPostProcessBeforeInit() throws Exception {
        //Arrange
        ValidBean validBean = new ValidBean();

        //Act
        executeAndConfirmSameBean(validBean);

        //Assert
        Logger mockedLogger = validBean.logger;
        assertNotNull(mockedLogger);
        assertEquals(mockedLogger.getName(), validBean.getClass().getName());
        assertNull(validBean.randomVar);
        PowerMockito.verifyStatic(times(1));
    }

    private class ValidBean {
        @InjectLogger
        private Logger logger;
        private String randomVar;
    }

    @Test
    public void testValidNamedPostProcessBeforeInit() throws Exception {
        //Arrange
        ValidNamedBean namedBean = new ValidNamedBean();

        //Act
        executeAndConfirmSameBean(namedBean);

        //Assert
        assertEquals(ValidNamedBean.NAMED_LOGGER, namedBean.logger.getName());
    }

    private class ValidNamedBean {
        public static final String NAMED_LOGGER = "NamedLogger";
        @InjectLogger(NAMED_LOGGER)
        private Logger logger;
    }

    @Test
    public void testInvalidTypePostProcessBeforeInit() throws Exception {
        //Arrange
        InvalidTypeBean invalidTypeBean = new InvalidTypeBean();

        //Act
        executeAndConfirmSameBean(invalidTypeBean);

        //Assert
        assertNull(invalidTypeBean.logger);
    }

    private class InvalidTypeBean {
        @InjectLogger
        private String logger;
    }

    @Test
    public void testMissingAnnotationPostProcessBeforeInit() throws Exception {
        //Arrange
        MissingAnnotationBean missingAnnotationBean = new MissingAnnotationBean();

        //Act
        executeAndConfirmSameBean(missingAnnotationBean);

        //Assert
        assertNull(missingAnnotationBean.logger);
    }

    private class MissingAnnotationBean {
        private Logger logger;
    }

    private void executeAndConfirmSameBean(Object bean) {
        //Act
        Object resultingBean = loggerProcessor.postProcessBeforeInitialization(bean, bean.getClass().getSimpleName());

        //Assert
        assertEquals(bean, resultingBean);
    }

    @Test
    public void testPostProcessAfterInitialization() throws Exception {
        //Act
        Object bean = loggerProcessor.postProcessAfterInitialization(null, null);

        //Assert
        assertNull(bean);
    }

}