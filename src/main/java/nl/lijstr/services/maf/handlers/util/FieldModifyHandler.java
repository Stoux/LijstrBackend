package nl.lijstr.services.maf.handlers.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import nl.lijstr.common.Container;
import nl.lijstr.common.ReflectUtils;
import nl.lijstr.common.StrUtils;
import nl.lijstr.domain.base.IdModel;
import nl.lijstr.domain.other.FieldHistory;
import nl.lijstr.domain.other.FieldHistorySuggestion;
import nl.lijstr.exceptions.LijstrException;
import nl.lijstr.repositories.other.FieldHistoryRepository;
import nl.lijstr.repositories.other.FieldHistorySuggestionRepository;
import org.springframework.util.ReflectionUtils;

/**
 * A handler that can easily modify Field values while keeping track of history.
 */
public class FieldModifyHandler {

    private IdModel o1;
    private Object o2;
    /** Should use the properties of the API object instead of methods. */
    private final boolean useApiProperties;

    private FieldHistoryRepository historyRepository;
    private FieldHistorySuggestionRepository suggestionRepository;
    private Map<String, FieldHistory> fieldHistoryMap;

    /** Any property is updated */
    private boolean updated;

    /**
     * Create a FieldModifyHandler.
     *
     * @param o1                   The original object
     * @param o2                   The new object
     * @param useApiProperties     Should properties of the API (new) object instead of it's methods
     * @param historyRepository    The FieldHistory repository
     * @param suggestionRepository The FieldHistorySuggestion repository
     */
    public FieldModifyHandler(IdModel o1, Object o2, boolean useApiProperties,
                              FieldHistoryRepository historyRepository,
                              FieldHistorySuggestionRepository suggestionRepository) {
        this.o1 = o1;
        this.o2 = o2;
        this.useApiProperties = useApiProperties;
        this.historyRepository = historyRepository;
        this.suggestionRepository = suggestionRepository;
        this.updated = false;

        fieldHistoryMap = new HashMap<>();
        fillHistoryMap();
    }

    /**
     * Create a FieldModifyHandler.
     *
     * @param o1                   The original object
     * @param o2                   The new object
     * @param historyRepository    The FieldHistory repository
     * @param suggestionRepository The FieldHistorySuggestion repository
     */
    public FieldModifyHandler(IdModel o1, Object o2,
                              FieldHistoryRepository historyRepository,
                              FieldHistorySuggestionRepository suggestionRepository) {
         this(o1, o2, false, historyRepository, suggestionRepository);
    }

    private void fillHistoryMap() {
        //Fill the history map
        this.fieldHistoryMap = new HashMap<>();
        if (o1.getId() == null) {
            // New item
            return;
        }

        List<FieldHistory> fieldHistoryList = historyRepository.findByObjectIdAndClassName(
                o1.getId(),
                FieldHistory.getDatabaseClassName(o1.getClass())
        );
        for (FieldHistory history : fieldHistoryList) {
            fieldHistoryMap.put(history.getField(), history);
        }
    }


    /**
     * Modify by fieldname.
     *
     * @param fieldName the field name in both Movie & ApiMovie
     */
    public void modify(String fieldName) {
        modify(fieldName, fieldName);
    }

    /**
     * Modify by fieldnames.
     *
     * @param fieldName    The Movie fieldname
     * @param apiFieldName The ApiMovie fieldname
     */
    public void modify(String fieldName, String apiFieldName) {
        Object newValue = getApiFieldValue(apiFieldName);
        modifyWithValue(fieldName, newValue);
    }

    /**
     * Modify the field of object 1 with the passed value.
     * @param fieldName The Entity field name
     * @param newValue  The Api/new field value
     */
    public void modifyWithValue(String fieldName, Object newValue) {
        Container<Method> movieFieldSetter = new Container<>();
        Object originalValue = getFieldValue(o1, fieldName, movieFieldSetter);

        compareAndModify(fieldName, originalValue, newValue, o -> o, object -> {
            Method method = movieFieldSetter.getItem();
            ReflectionUtils.invokeMethod(method, o1, object);
        });
    }


    /**
     * @see FieldModifyHandler#compareAndModify(String, Object, Object, Function, Consumer)
     */
    public <X> void modify(String fieldName, X originalValue, X newValue, Consumer<X> setterFunction) {
        compareAndModify(fieldName, originalValue, newValue, x -> x, setterFunction);
    }

    /**
     * Compare and modify 2 values.
     *
     * @param fieldName         The name of the field
     * @param originalValue     The original value
     * @param newValue          The new value
     * @param transformFunction A function to convert newValue to the same type as originalValue
     * @param setterFunction    A setter function to set the new value
     * @param <X>               The class of the original value
     * @param <Y>               The class of the new value
     */
    public <X, Y> void compareAndModify(String fieldName, X originalValue, Y newValue,
                                        Function<Y, X> transformFunction,
                                        Consumer<X> setterFunction) {
        //Get the modified value
        X modifiedValue = null;
        if (newValue != null && !(newValue instanceof String && ((String) newValue).isEmpty())) {
            modifiedValue = transformFunction.apply(newValue);
        }

        //Check if equal
        if (areEqual(originalValue, modifiedValue)) {
            if (fieldHistoryMap.containsKey(fieldName)) {
                //The overruled value has become the new default value
                FieldHistory fieldHistory = fieldHistoryMap.get(fieldName);
                historyRepository.delete(fieldHistory);
            }
            return;
        }

        //Check for changes
        if (fieldHistoryMap.containsKey(fieldName)) {
            FieldHistory fieldHistory = fieldHistoryMap.get(fieldName);
            String newValueAsString = StrUtils.stringOrNull(modifiedValue);
            if (areEqual(fieldHistory.getOldValue(), newValueAsString)) {
                //Trying to re-apply an overruled value
                return;
            }

            //A new suggestion
            FieldHistorySuggestion suggestion = new FieldHistorySuggestion(fieldHistory, newValueAsString);
            suggestionRepository.saveAndFlush(suggestion);
            return;
        }

        updated = true;
        setterFunction.accept(modifiedValue);
    }

    private boolean areEqual(Object o1, Object o2) {
        if (o1 == null && o2 == null) {
            return true;
        }

        if (o1 == null || o2 == null) {
            return false;
        }

        return o1.equals(o2);
    }

    @SuppressWarnings("unchecked")
    private <X> X getApiFieldValue(String fieldName) {
        if (useApiProperties) {
            final Class<?> aClass = o2.getClass();
            try {
                final Field field = aClass.getField(fieldName);
                return (X) field.get(o2);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new LijstrException("Failed to find field for " + aClass.getSimpleName() + " - "  + fieldName);
            }
        } else {
            return getFieldValue(o2, fieldName, null);
        }
    }

    @SuppressWarnings("unchecked")
    private <X> X getFieldValue(Object onObject, String fieldName, Container<Method> setterContainer) {
        Class<?> aClass = onObject.getClass();
        Container<Method> methodContainer = new Container<>();
        Consumer<Method> setterConsumer = setterContainer == null ? null : setterContainer::setItem;
        if (!ReflectUtils.findFieldMethods(aClass, fieldName, methodContainer::setItem, setterConsumer)) {
            throw new LijstrException("Failed to find Getter for: " + aClass.getSimpleName() + " - " + fieldName);
        }

        try {
            return (X) methodContainer.getItem().invoke(onObject);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new LijstrException(e);
        }
    }

    /**
     * @return if any property is updated on the main model
     */
    public boolean isUpdated() {
        return updated;
    }
}
