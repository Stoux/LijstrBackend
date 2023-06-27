package nl.lijstr.processors;

import nl.lijstr.common.StrUtils;
import nl.lijstr.processors.abs.AbsBeanProcessor;
import nl.lijstr.processors.annotations.InjectLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * A Bean processor that injects Logger instances into Beans.
 */
@Component
public class LoggerProcessor extends AbsBeanProcessor<InjectLogger> {

    /**
     * Create a LoggerProcessor.
     */
    public LoggerProcessor() {
        super(InjectLogger.class);
    }

    @Override
    protected boolean qualifies(Object bean, Field field, InjectLogger annotation) {
        return Logger.class.isAssignableFrom(field.getType());
    }

    @Override
    protected Object getInjectObject(Object bean, Field field, InjectLogger annotation) {
        String name = StrUtils.useOrDefault(annotation.value(), bean.getClass().getName());
        return LogManager.getLogger(name);
    }

}
