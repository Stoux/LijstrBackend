package nl.lijstr.services.modify;

import java.lang.reflect.Field;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.persistence.*;
import nl.lijstr.common.ReflectUtils;
import nl.lijstr.common.StrUtils;
import nl.lijstr.common.Utils;
import nl.lijstr.domain.base.IdModel;
import nl.lijstr.processors.annotations.InjectLogger;
import nl.lijstr.repositories.abs.BasicRepository;
import nl.lijstr.services.modify.annotations.ExternalModifiable;
import nl.lijstr.services.modify.annotations.ModifiableWithHistory;
import nl.lijstr.services.modify.annotations.NotModifiable;
import nl.lijstr.services.modify.models.ReflectedField;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

/**
 * A service that allows easy modification of entities by using annotations.
 * <p>
 * After being constructed this class will find all entities that allow for modifications
 * and registers them. After that the function {@link #modify(BasicRepository, IdModel, IdModel)} is
 * available to actually modify an entity with new values.
 */
@Service
public class ModelModifyService {

    public static final String DOMAIN_PACKAGE = "nl.lijstr.domain";

    @InjectLogger("ModifyServices")
    private static Logger logger;
    private final Map<Class<?>, List<ReflectedField>> classToReflectedFields;

    @Autowired
    private FieldModifierService fieldModifier;

    /**
     * Create a ModelModifyService.
     */
    public ModelModifyService() {
        classToReflectedFields = new HashMap<>();
    }

    /**
     * Find the Getter & Setter methods for a certain field.
     */
    private static boolean findFieldMethods(final Class<?> clazz, final ReflectedField reflectedField) {
        return ReflectUtils.findFieldMethods(
                clazz, reflectedField.getFieldName(),
                reflectedField::setGetterMethod, reflectedField::setSetterMethod
        );
    }

    /**
     * Modify the fields of a certain IdModel object.
     *
     * @param xRepository The repository to save X
     * @param original    The original object
     * @param modifyWith  The modified values
     * @param <X>         The class of the model
     *
     * @return the new saved version of the original
     */
    @Transactional
    public <X extends IdModel> Optional<X> modify(final BasicRepository<X> xRepository,
                                                  final X original,
                                                  final X modifyWith) {
        //Check if the object is registered
        if (!classToReflectedFields.containsKey(original.getClass())) {
            return Optional.empty();
        }

        return fieldModifier.modify(
                xRepository, original, modifyWith,
                classToReflectedFields.get(original.getClass())
        );
    }

    /**
     * Get the List of reflected fields for a certain class.
     *
     * @param clazz The class
     *
     * @return the list
     */
    public List<ReflectedField> getReflectedFields(Class<?> clazz) {
        return classToReflectedFields.get(clazz);
    }

    @SuppressWarnings("squid:UnusedPrivateMethod")
    @PostConstruct
    private void construct() throws ClassNotFoundException {
        ClassPathScanningCandidateComponentProvider scanner = getClassPathScanner();
        scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
        scanner.addExcludeFilter(new AnnotationTypeFilter(NotModifiable.class));

        for (BeanDefinition beanDefinition : scanner.findCandidateComponents(DOMAIN_PACKAGE)) {
            //Try to reflect the field
            String className = beanDefinition.getBeanClassName();
            Class<?> clazz = loadClass(className);
            List<ReflectedField> reflectedFields = loadClassFields(clazz);

            //Log it
            logger.debug(
                    "Mapped {} fields for class: {} ({})",
                    reflectedFields.size(), className,
                    StrUtils.collectionToDelimitedString(
                            reflectedFields, ReflectedField::getFieldName, ", "
                    )
            );
        }
    }

    private Class<?> loadClass(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }

    private ClassPathScanningCandidateComponentProvider getClassPathScanner() {
        return new ClassPathScanningCandidateComponentProvider(false);
    }

    /**
     * Load Fields for this Class.
     *
     * @param clazz The class
     *
     * @return the list of fields
     */
    private List<ReflectedField> loadClassFields(Class<?> clazz) {
        final boolean classKeepsHistory = keepsHistory(clazz);
        List<ReflectedField> reflectedFields = new ArrayList<>();

        //Loop through fields, check if modifiable and reflect it for later usage
        ReflectionUtils.doWithFields(clazz, field -> {
            if (isModifiable(field)) {
                ReflectedField reflectedField = new ReflectedField(field);
                if (findFieldMethods(clazz, reflectedField)) {
                    reflectedField.setKeepHistory(classKeepsHistory || keepsHistory(field));
                    reflectedFields.add(reflectedField);
                }
            }
        });

        classToReflectedFields.put(clazz, reflectedFields);
        return reflectedFields;
    }

    /**
     * Check if a class keeps history.
     */
    private boolean keepsHistory(Class<?> clazz) {
        ModifiableWithHistory withHistory = AnnotationUtils.findAnnotation(clazz, ModifiableWithHistory.class);
        return withHistory != null;
    }

    /**
     * Check if a field keeps history.
     */
    private boolean keepsHistory(Field field) {
        return Utils.hasAnnotation(field, ModifiableWithHistory.class);
    }

    /**
     * Check if a field can be modified.
     */
    private boolean isModifiable(Field field) {
        //Doesn't allow for NotModifiable or Id
        if (Utils.hasOneofAnnotations(field, NotModifiable.class, Id.class)) {
            return false;
        }

        //If external => Require ExternalModifiable
        if (Utils.hasOneofAnnotations(field, OneToOne.class, OneToMany.class, ManyToOne.class, ManyToMany.class)) {
            if (!Utils.hasAnnotation(field, ExternalModifiable.class)) {
                return false;
            }
        }

        return true;
    }

}
