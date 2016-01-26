package nl.lijstr.repositories.abs;

import java.util.List;
import nl.lijstr.domain.users.User;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Create a BasicMultipleUserRepository.
 * This repository contains an items that belong to a User.
 *
 * @param <X> The model
 */
@NoRepositoryBean
public interface BasicMultipleUserRepository<X> extends BasicRepository<X> {

    /**
     * Find a X by their User.
     *
     * @param user The user
     *
     * @return The object or null
     */
    List<X> findByUser(User user);

}
