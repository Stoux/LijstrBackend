package nl.lijstr.common;

import java.util.function.Supplier;

/**
 * A container class to hold a certain item.
 * This container also has the ability to generate an item if needed.
 *
 * @param <X> The class of the item in the container
 */
public class CreatingContainer<X> extends Container<X> {

    private Supplier<X> createMethod;

    public CreatingContainer(Supplier<X> createMethod) {
        this.createMethod = createMethod;
    }

    public CreatingContainer(X item, Supplier<X> createMethod) {
        super(item);
        this.createMethod = createMethod;
    }

    @Override
    public X getItem() {
        if (!isPresent()) {
            X item = createMethod.get();
            setItem(item);
        }
        return super.getItem();
    }

}