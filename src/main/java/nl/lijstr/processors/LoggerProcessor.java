package nl.lijstr.processors;

import java.lang.reflect.Field;
import nl.lijstr.common.Container;
import nl.lijstr.processors.annotations.InjectLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * A Bean processor that injects Logger instances into Beans.
 */
@Component
public class LoggerProcessor implements BeanPostProcessor {


    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        Container<InjectLogger> annContainer = new Container<>();
        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            if (isLoggerType(field) && hasInjectLoggerAnnotation(field, annContainer)) {
                injectLogger(bean, field, annContainer.getItem());
            }
        });
        return bean;
    }

    private boolean isLoggerType(Field field) {
        return Logger.class.isAssignableFrom(field.getType());
    }

    private boolean hasInjectLoggerAnnotation(Field field, Container<InjectLogger> container) {
        InjectLogger injectLogger = field.getDeclaredAnnotation(InjectLogger.class);
        container.setItem(injectLogger);
        return injectLogger != null;
    }

    private void injectLogger(Object bean, Field field, InjectLogger injectLogger) {
        String name = injectLogger.value().isEmpty() ? bean.getClass().getName() : injectLogger.value();
        Logger logger = LogManager.getLogger(name);
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, bean, logger);
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}
