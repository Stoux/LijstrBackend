package nl.lijstr.api.abs;

import java.util.List;
import javax.annotation.PostConstruct;
import nl.lijstr.domain.base.IdModel;
import nl.lijstr.exceptions.db.NotFoundException;
import nl.lijstr.repositories.abs.BasicRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * An Abstract RestService that contains a couple of
 * useful methods for basic REST usage.
 *
 * @param <X> The type provided by this service
 */
public abstract class AbsRestService<X extends IdModel> extends AbsService {

    protected String itemName;
    protected BasicRepository<X> basicRepository;

    /**
     * Create an Abstract RestService.
     *
     * @param itemName The name of the item
     */
    protected AbsRestService(String itemName) {
        this.itemName = itemName;
    }

    /**
     * Get an entity by it's ID.
     *
     * @param id the id
     *
     * @return the value
     */
    @RequestMapping("/id")
    public X getById(@PathVariable("id") final long id) {
        X foundItem = basicRepository.findOne(id);
        if (foundItem == null) {
            throw new NotFoundException(itemName, id);
        }
        return foundItem;
    }

    /**
     * Get all entities.
     *
     * @return the entities
     */
    @RequestMapping
    public List<X> findAll() {
        return basicRepository.findAll();
    }

    @SuppressWarnings("squid:UnusedPrivateMethod")
    @PostConstruct
    private void fillValues() {
        this.basicRepository = getRestRepository();
    }

    /**
     * Provide a basicRepository for this REST service.
     * <p>
     * This method is called after construction of the controller through
     * the post construct method #fillValues();
     *
     * @return the service
     */
    protected abstract BasicRepository<X> getRestRepository();

}
