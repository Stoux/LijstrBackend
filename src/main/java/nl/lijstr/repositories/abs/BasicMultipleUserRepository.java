package nl.lijstr.repositories.abs;

import java.util.List;
import nl.lijstr.domain.users.User;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Create a BasicMultipleUserRepository.
 * This repository contains items that belong to a User.
 *
 * @param <T> The model
 */
@NoRepositoryBean
public interface BasicMultipleUserRepository<T> extends BasicRepository<T> {

    /**
     * Find a X by their User.
     *
     * @param user The user
     *
     * @return the object or null
     */
    List<T> findByUser(User user);

}
