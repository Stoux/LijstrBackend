package nl.lijstr.processors.abs;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import nl.lijstr.common.Utils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;

/**
 * An Abstract Bean Processor.
 * Includes logic to reflect every field on the bean.
 *
 * @param <X> The class of the annotation
 */
public abstract class AbsBeanProcessor<X extends Annotation> implements BeanPostProcessor {

    private Class<X> annotationClass;

    /**
     * Create an AbsBeanProcessor.
     *
     * @param annotationClass The class of the annotation.
     */
    protected AbsBeanProcessor(Class<X> annotationClass) {
        this.annotationClass = annotationClass;
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(), field -> processField(bean, field));
        return bean;
    }

    private void processField(Object bean, Field field) {
        //Find the annotation & check if it qualifies
        X annotation = Utils.getAnnotation(field, annotationClass);
        if (annotation == null || !qualifies(bean, field, annotation)) {
            return;
        }

        //Inject the object
        Object toInject = getInjectObject(bean, field, annotation);
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, bean, toInject);
    }

    /**
     * Check if the field qualifies for injection.
     *
     * @param bean       the bean
     * @param field      the field
     * @param annotation the annotation
     *
     * @return is qualified for injection
     */
    protected boolean qualifies(Object bean, Field field, X annotation) {
        return true;
    }

    ;

    /**
     * Get the object that needs to be injected into the field.
     *
     * @param bean       the bean
     * @param field      the field
     * @param annotation the annotation
     *
     * @return the object
     */
    protected abstract Object getInjectObject(Object bean, Field field, X annotation);


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
