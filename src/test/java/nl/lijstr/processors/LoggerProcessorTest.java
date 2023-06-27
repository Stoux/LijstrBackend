package nl.lijstr.processors;

import nl.lijstr.processors.annotations.InjectLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Created by Stoux on 27/01/2016.
 */
@ExtendWith(MockitoExtension.class)
public class LoggerProcessorTest {

    private LoggerProcessor loggerProcessor;

    @BeforeEach
    public void setUp() {
        loggerProcessor = new LoggerProcessor();
    }

    private static void runWithMockedLogManager(Consumer<MockedStatic<LogManager>> runnable) {
        //Setup the LogManager to return a mocked logger using the passed param.
        try (MockedStatic<LogManager> logManager = mockStatic(LogManager.class)) {
            logManager.when(() -> LogManager.getLogger(anyString())).thenAnswer(invocation -> {
                //Get passed argument
                final String loggerName = (String) invocation.getArguments()[0];

                //Mock the logger
                Logger mockLogger = mock(Logger.class);
                when(mockLogger.getName()).thenReturn(loggerName);

                return mockLogger;
            });

            runnable.accept(logManager);
        }
    }


    @Test
    public void testValidPostProcessBeforeInit()  {
        runWithMockedLogManager(logManager -> {
            //Arrange
            ValidBean validBean = new ValidBean();

            //Act
            executeAndConfirmSameBean(validBean);

            //Assert
            Logger mockedLogger = validBean.logger;
            assertNotNull(mockedLogger);
            assertEquals(mockedLogger.getName(), validBean.getClass().getName());
            assertNull(validBean.randomVar);
            logManager.verify(() -> LogManager.getLogger(anyString()), times(1));
        });
    }

    @Test
    public void testValidNamedPostProcessBeforeInit() {
        runWithMockedLogManager(logManager -> {
            //Arrange
            ValidNamedBean namedBean = new ValidNamedBean();

            //Act
            executeAndConfirmSameBean(namedBean);

            //Assert
            assertEquals(ValidNamedBean.NAMED_LOGGER, namedBean.logger.getName());
        });
    }

    @Test
    public void testInvalidTypePostProcessBeforeInit() {
        runWithMockedLogManager(logManager -> {

        });
        //Arrange
        InvalidTypeBean invalidTypeBean = new InvalidTypeBean();

        //Act
        executeAndConfirmSameBean(invalidTypeBean);

        //Assert
        assertNull(invalidTypeBean.logger);
    }

    @Test
    public void testMissingAnnotationPostProcessBeforeInit() {
        runWithMockedLogManager(logManager -> {
            //Arrange
            MissingAnnotationBean missingAnnotationBean = new MissingAnnotationBean();

            //Act
            executeAndConfirmSameBean(missingAnnotationBean);

            //Assert
            assertNull(missingAnnotationBean.logger);
        });
    }

    private void executeAndConfirmSameBean(Object bean) {
        //Act
        Object resultingBean = loggerProcessor.postProcessBeforeInitialization(bean, bean.getClass().getSimpleName());

        //Assert
        assertEquals(bean, resultingBean);
    }

    @Test
    public void testPostProcessAfterInitialization() {
        runWithMockedLogManager(logManager -> {
            //Act
            Object bean = loggerProcessor.postProcessAfterInitialization(null, null);

            //Assert
            assertNull(bean);
        });
    }

    private static class ValidBean {
        @InjectLogger
        private Logger logger;
        private String randomVar;
    }

    private static class ValidNamedBean {
        public static final String NAMED_LOGGER = "NamedLogger";
        @InjectLogger(NAMED_LOGGER)
        private Logger logger;
    }

    private static class InvalidTypeBean {
        @InjectLogger
        private String logger;
    }

    private static class MissingAnnotationBean {
        private Logger logger;
    }

}