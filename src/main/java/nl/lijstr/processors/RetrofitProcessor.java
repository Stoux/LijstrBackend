package nl.lijstr.processors;

import java.lang.reflect.Field;
import nl.lijstr.common.Utils;
import nl.lijstr.processors.abs.AbsBeanProcessor;
import nl.lijstr.processors.annotations.InjectRetrofitService;
import nl.lijstr.services.retrofit.RetrofitService;
import nl.lijstr.services.retrofit.annotations.RetrofitServiceAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A BeanPostProcessor that injects Retrofit Service instances into beans.
 */
@Component
public class RetrofitProcessor extends AbsBeanProcessor<InjectRetrofitService> {

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
        return retrofitService.createRetrofitService(serviceAnnotation.value(), field.getType());
    }

}
