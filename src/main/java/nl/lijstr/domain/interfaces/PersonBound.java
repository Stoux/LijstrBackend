package nl.lijstr.domain.interfaces;

import nl.lijstr.domain.imdb.Person;

/**
 * Indicates an object that is bound to a {@link nl.lijstr.domain.imdb.Person}.
 */
public interface PersonBound extends ImdbIdentifiable {

    /**
     * Get the person.
     *
     * @return the person
     */
    Person getPerson();

}
