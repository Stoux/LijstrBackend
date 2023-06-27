package nl.lijstr.api.abs;

import nl.lijstr.common.Utils;
import nl.lijstr.domain.imdb.Person;
import nl.lijstr.domain.interfaces.MovieBound;
import nl.lijstr.domain.interfaces.PersonBound;
import nl.lijstr.exceptions.db.NotFoundException;
import nl.lijstr.repositories.abs.PersonBoundRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A basic endpoint that exposes fetch methods for {@link Person}s that are linked to a {@link nl.lijstr.domain.movies.Movie}.
 *
 * @param <T> The model class
 */
public class AbsMoviePersonService<T extends PersonBound & MovieBound> extends AbsService {

    protected PersonBoundRepository<T> repository;

    public AbsMoviePersonService(PersonBoundRepository<T> repository) {
        this.repository = repository;
    }

    /**
     * Get a map of all the people bound to this model.
     *
     * @param optName Optional filter on the name
     *
     * @return the map with IDs to names
     */
    @RequestMapping(value = "/")
    public Map<Long, String> findPeople(@RequestParam(name = "name", required = false) Optional<String> optName) {
        List<Person> directors = repository.findAllWithNameContaining(optName.orElse(""));
        return Utils.toMap(directors, Person::getId, Person::getName);
    }

    /**
     * Get all movies this person has done with this model.
     *
     * @param id The ID of the person
     *
     * @return the list of movies, ID to name
     */
    @RequestMapping(value = "/{id:\\d+}")
    public Map<Long, String> getMoviesWithPerson(@PathVariable("id") final long id) {
        List<T> directedBy = repository.findAllByPersonId(id);
        if (directedBy == null || directedBy.isEmpty()) {
            throw new NotFoundException("This person doesn't exist or hasn't directed any movies");
        }
        return Utils.toMap(directedBy, md -> md.getMovie().getId(), md -> md.getMovie().getTitle());
    }

}
