package nl.lijstr.domain.movies.people;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.*;
import nl.lijstr.domain.imdb.AbstractActor;
import nl.lijstr.domain.movies.Movie;

/**
 * Created by Stoux on 03/12/2015.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MovieActor extends AbstractActor {

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Movie movie;

}
