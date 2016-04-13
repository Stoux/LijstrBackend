package nl.lijstr.domain.movies.people;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.*;
import nl.lijstr.domain.base.IdModel;
import nl.lijstr.domain.imdb.Person;
import nl.lijstr.domain.interfaces.PersonBound;
import nl.lijstr.domain.movies.Movie;

/**
 * A link between a {@link Movie} and an IMDB {@link Person} who has <strong>written</strong> that movie.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MovieWriter extends IdModel implements PersonBound {

    @JsonBackReference
    @ManyToOne
    private Movie movie;
    @JsonManagedReference
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Person person;

    @Override
    public String getImdbId() {
        return person.getImdbId();
    }
}
