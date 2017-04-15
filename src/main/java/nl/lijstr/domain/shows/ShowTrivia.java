package nl.lijstr.domain.shows;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import lombok.*;
import nl.lijstr.domain.base.IdModel;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.shows.base.ShowBoundModel;

/**
 * Trivia/facts about a {@link Show}.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ShowTrivia extends ShowBoundModel {

    @Lob
    @Column(nullable = false)
    private String trivia;

}
