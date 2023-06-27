package nl.lijstr.domain.movies.people;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.lijstr.domain.base.IdModel;
import nl.lijstr.domain.imdb.Person;
import nl.lijstr.domain.interfaces.MovieBound;
import nl.lijstr.domain.interfaces.PersonBound;
import nl.lijstr.domain.movies.Movie;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

/**
 * A link between a {@link Movie} and an IMDB {@link Person} who has <strong>directed</strong> that movie.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MovieDirector extends IdModel implements PersonBound, MovieBound {

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
