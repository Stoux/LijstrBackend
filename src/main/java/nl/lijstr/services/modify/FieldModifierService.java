package nl.lijstr.services.modify;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import nl.lijstr.common.Container;
import nl.lijstr.domain.base.IdModel;
import nl.lijstr.domain.other.FieldHistory;
import nl.lijstr.processors.annotations.InjectLogger;
import nl.lijstr.repositories.abs.BasicRepository;
import nl.lijstr.repositories.other.FieldHistoryRepository;
import nl.lijstr.services.modify.models.ReflectedField;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A service that provides the ability to automatically modify fields based on annotations.
 * Used in conjunction with {@link FieldModifierService}.
 */
@Service
public class FieldModifierService {

    @InjectLogger("ModifyServices")
    private static Logger logger;

    @Autowired
    private FieldHistoryRepository historyRepository;

    /**
     * Modify the fields of a certain IdModel object.
     *
     * @param xRepository     The repository to save X
     * @param original        The original object
     * @param modifyWith      The modified values
     * @param reflectedFields The list of reflected fields
     * @param <X>             The class of the model
     *
     * @return the new saved version of the original
     */
    public <X extends IdModel> Optional<X> modify(final BasicRepository<X> xRepository,
                                                  final X original,
                                                  final X modifyWith,
                                                  final List<ReflectedField> reflectedFields) {
        //Modify the fields
        Container<Boolean> modifiedContainer = new Container<>(Boolean.FALSE);
        reflectedFields.forEach(reflectedField -> {
            try {
                boolean isModified = modifyField(reflectedField, original, modifyWith);
                if (isModified) {
                    modifiedContainer.setItem(Boolean.TRUE);
                }
            } catch (ReflectiveOperationException e) {
                logger.warn(
                        "Failed to update field: {} (Class: {})",
                        reflectedField.getFieldName(), original.getClass().getName(), e
                );
            }
        });

        //Update repository if needed
        if (modifiedContainer.getItem()) {
            X xSaved = xRepository.saveAndFlush(original);
            return Optional.of(xSaved);
        } else {
            return Optional.empty();
        }
    }

    private <X extends IdModel> boolean modifyField(final ReflectedField reflectedField,
                                                    final X original,
                                                    final X modifyWith) throws ReflectiveOperationException {
        //Extract & compare
        final Method getterMethod = reflectedField.getGetterMethod();
        Object originalValue = getterMethod.invoke(original);
        Object modifiedValue = getterMethod.invoke(modifyWith);
        if (isSame(originalValue, modifiedValue)) {
            return false;
        }

        //Update value
        reflectedField.getSetterMethod().invoke(original, modifiedValue);

        //Keep history if needed
        if (reflectedField.keepsHistory()) {
            FieldHistory history = FieldHistory.asFieldHistory(
                    original.getClass(),
                    original.getId(),
                    reflectedField.getField(),
                    asString(originalValue),
                    asString(modifiedValue)
            );
            historyRepository.save(history);
        }

        return true;
    }


    private boolean isSame(Object originalValue, Object modifiedValue) {
        //Could be replaced with a single statement but found it less clear
        if (originalValue == null && modifiedValue == null) {
            return true;
        }

        if (originalValue == null || modifiedValue == null) {
            return false;
        }

        return originalValue.equals(modifiedValue);
    }

    private String asString(Object value) throws IllegalAccessException {
        if (value == null) {
            return null;
        } else {
            return value.toString();
        }
    }

}
