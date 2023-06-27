package nl.lijstr.domain.movies.people;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.lijstr.domain.imdb.AbstractActor;
import nl.lijstr.domain.imdb.Person;
import nl.lijstr.domain.interfaces.MovieBound;
import nl.lijstr.domain.movies.Movie;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

/**
 * A {@link Person} that played in a {@link Movie}.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class MovieCharacter extends AbstractActor implements MovieBound {

    @JsonBackReference
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Movie movie;

    /**
     * Create a {@link MovieCharacter}.
     *
     * @param movie         The movie
     * @param person        The person
     * @param character     The name of the character
     * @param characterUrl  An optional URL to the character's IMDB page
     * @param photoUrl      An optional URL to the character's picture
     * @param mainCharacter is a main character
     */
    public MovieCharacter(Movie movie, Person person, String character, String characterUrl, String photoUrl,
                          boolean mainCharacter) {
        super(person, character, characterUrl, photoUrl, mainCharacter);
        this.movie = movie;
    }

}
