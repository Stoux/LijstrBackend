package nl.lijstr.domain.movies;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import lombok.*;
import nl.lijstr.domain.base.IdModel;

/**
 * Created by Stoux on 03/12/2015.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MovieTrivia extends IdModel {

    @ManyToOne
    private Movie movie;

    @Lob
    private String trivia;

}
