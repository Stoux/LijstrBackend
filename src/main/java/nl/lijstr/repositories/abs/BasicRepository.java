package nl.lijstr.repositories.abs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * A interface that provides a simple way to create a repository
 * with default features like findAll().
 *
 * @param <T> The class of the Model
 */
@NoRepositoryBean
public interface BasicRepository<T> extends JpaRepository<T, Long> {

}
