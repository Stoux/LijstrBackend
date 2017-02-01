package nl.lijstr.processors;

import java.lang.reflect.Field;
import nl.lijstr.common.Utils;
import nl.lijstr.exceptions.LijstrException;
import nl.lijstr.processors.abs.AbsBeanProcessor;
import nl.lijstr.processors.annotations.InjectLogger;
import nl.lijstr.processors.annotations.InjectRetrofitService;
import nl.lijstr.services.retrofit.RetrofitService;
import nl.lijstr.services.retrofit.annotations.RetrofitServiceAnnotation;
import nl.lijstr.services.retrofit.models.TimeoutTimings;
import okhttp3.Interceptor;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * A BeanPostProcessor that injects Retrofit Service instances into beans.
 */
@Component
public class RetrofitProcessor extends AbsBeanProcessor<InjectRetrofitService> {

    @InjectLogger
    private Logger logger;

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @Autowired
    private RetrofitService retrofitService;

    /**
     * Create a RetrofitProcessor.
     */
    public RetrofitProcessor() {
        super(InjectRetrofitService.class);
    }

    @Override
    protected boolean qualifies(Object bean, Field field, InjectRetrofitService annotation) {
        return Utils.hasAnnotation(field.getType(), RetrofitServiceAnnotation.class);
    }

    @Override
    protected Object getInjectObject(Object bean, Field field, InjectRetrofitService annotation) {
        RetrofitServiceAnnotation serviceAnnotation = Utils.getAnnotation(field.getType(), RetrofitServiceAnnotation.class);

        //Create injectors if needed
        Interceptor interceptor = null;
        Class<? extends Interceptor> interceptorClass = serviceAnnotation.interceptorClass();
        if (!interceptorClass.equals(Interceptor.class)) {
            try {
                interceptor = interceptorClass.newInstance();
                if (serviceAnnotation.springInjectInterceptor()) {
                    beanFactory.autowireBean(interceptor);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                logger.fatal("Failed to create interceptor: {}", e.getMessage());
                logger.fatal(e);
                throw new LijstrException("Failed to create Interceptor of class: " + interceptorClass.getName());
            }
        }

        TimeoutTimings timings = new TimeoutTimings(
                serviceAnnotation.connectTimeout(), serviceAnnotation.readTimeout(), serviceAnnotation.writeTimeout()
        );

        if (interceptor == null) {
            return retrofitService.createRetrofitService(serviceAnnotation.value(), field.getType(), timings);
        } else {
            return retrofitService.createRetrofitService(
                    serviceAnnotation.value(), field.getType(), timings, interceptor
            );
        }
    }


}
