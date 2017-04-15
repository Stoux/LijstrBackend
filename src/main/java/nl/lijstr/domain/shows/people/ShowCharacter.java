package nl.lijstr.domain.shows.people;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.*;
import nl.lijstr.domain.imdb.AbstractActor;
import nl.lijstr.domain.imdb.Person;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.shows.Show;

/**
 * A {@link Person} that played in a {@link Show}.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class ShowCharacter extends AbstractActor {

    @JsonBackReference
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Show show;

    /**
     * Create a {@link ShowCharacter}.
     *
     * @param show          The show
     * @param person        The person
     * @param character     The name of the character
     * @param characterUrl  An optional URL to the character's IMDB page
     * @param photoUrl      An optional URL to the character's picture
     * @param mainCharacter is a main character
     */
    public ShowCharacter(Show show, Person person, String character, String characterUrl, String photoUrl,
                         boolean mainCharacter) {
        super(person, character, characterUrl, photoUrl, mainCharacter);
        this.show = show;
    }

}
