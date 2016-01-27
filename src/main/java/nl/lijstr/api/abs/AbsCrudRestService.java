package nl.lijstr.api.abs;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import nl.lijstr.common.Utils;
import nl.lijstr.domain.base.IdModel;
import nl.lijstr.services.modify.ModelModifyService;
import nl.lijstr.services.modify.models.ReflectedField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * An Abstract RestService that contains a couple of
 * useful methods for basic REST usage.
 * The one also uffers CRUD support.
 *
 * @param <X> The type provided by this service
 */
public abstract class AbsCrudRestService<X extends IdModel> extends AbsRestService<X> {

    @Autowired
    private ModelModifyService modelModifyService;

    private Class<X> xClass;

    protected AbsCrudRestService(Class<X> xClass) {
        this(xClass.getSimpleName(), xClass);
    }

    public AbsCrudRestService(String itemName, Class<X> xClass) {
        super(itemName);
        this.xClass = xClass;
    }

    /**
     * Modify an item using a PUT request.
     *
     * @param id             The ID of the item
     * @param modifiedValues The new modified values
     *
     * @return The item with the modified values
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<X> modify(@PathVariable("id") final long id, @RequestBody final X modifiedValues) {
        X foundItem = getById(id);
        boolean modified = modifyValues(foundItem, modifiedValues);
        return new ResponseEntity<>(foundItem, modified ? HttpStatus.OK : HttpStatus.NOT_MODIFIED);
    }

    /**
     * Modify the values on a PUT request.
     *
     * @param original  The original item (to be modified)
     * @param newValues The new values
     *
     * @return is modified
     */
    protected boolean modifyValues(X original, X newValues) {
        Optional<X> modifiedItem = modelModifyService.modify(basicRepository, original, newValues);
        return modifiedItem.isPresent();
    }

    /**
     * Return a list of all the fields that can be modified.
     *
     * @return the fields
     */
    @RequestMapping("/options")
    public Map modifiableFields() {
        //Get the ReflectedFields
        List<ReflectedField> reflectedFields = modelModifyService.getReflectedFields(xClass);
        List<String> fieldNames = reflectedFields.stream()
                .map(ReflectedField::getFieldName)
                .collect(Collectors.toList());

        //Return the fields
        return Utils.asMap("modifiable", fieldNames);
    }

    /**
     * Delete an entity by it's ID.
     *
     * @param id The ID
     *
     * @return An OK response status message
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public HttpEntity delete(@PathVariable("id") final long id) {
        X foundItem = getById(id);
        basicRepository.delete(foundItem);
        return ok(itemName + " with ID " + id + " has been deleted");
    }

    /**
     * Post a new entity.
     *
     * @param newItem the new entity
     *
     * @return the new entity
     */
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public X create(@RequestBody X newItem) {
        X validatedItem = validateNewItem(newItem);
        basicRepository.save(validatedItem);
        return validatedItem;
    }

    /**
     * Validates a new Enitty after being posted to #create().
     *
     * @param newItem The new item
     *
     * @return A possibly modified or new item
     */
    protected abstract X validateNewItem(X newItem);

}
