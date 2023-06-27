package nl.lijstr.repositories.abs;

import nl.lijstr.domain.imdb.Person;
import nl.lijstr.domain.interfaces.PersonBound;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * A repository for items that are bound to a {@link Person}.
 * Adds convenience methods to fetch all by a certain person or all people that have this object attachted to it.
 *
 * @param <T> The model
 */
@NoRepositoryBean
public interface PersonBoundRepository<T extends PersonBound> extends BasicRepository<T> {

    /**
     * Find by their Person's ID.
     *
     * @param id The ID of the person
     *
     * @return list of items
     */
    List<T> findAllByPersonId(long id);

    /**
     * Find all people that have a name containing the given param
     *
     * @param name The name
     *
     * @return list of people
     */
    @Query("SELECT DISTINCT p FROM #{#entityName} d LEFT JOIN d.person p WHERE p.name LIKE CONCAT('%', :name, '%')")
    List<Person> findAllWithNameContaining(@Param("name") String name);


}
