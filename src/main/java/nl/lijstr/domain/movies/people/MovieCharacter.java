package nl.lijstr.domain.movies.people;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.*;
import nl.lijstr.domain.imdb.AbstractActor;
import nl.lijstr.domain.imdb.Person;
import nl.lijstr.domain.movies.Movie;

/**
 * A {@link Person} that played in a {@link Movie}.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class MovieCharacter extends AbstractActor {

    /**
     * Create a {@link MovieCharacter}.
     *
     * @param person        The person
     * @param character     The name of the character
     * @param characterUrl  An optional URL to the character's IMDB page
     * @param photoUrl      An optional URL to the character's picture
     * @param mainCharacter is a main character
     */
    public MovieCharacter(Movie movie, Person person, String character, String characterUrl, String photoUrl, boolean mainCharacter) {
        super(person, character, characterUrl, photoUrl, mainCharacter);
        this.movie = movie;
    }

    @JsonBackReference
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Movie movie;

}
