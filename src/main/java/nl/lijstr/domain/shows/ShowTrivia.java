package nl.lijstr.domain.shows;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import lombok.*;
import nl.lijstr.domain.shows.base.ShowBoundModel;

/**
 * Trivia/facts about a {@link Show}.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class ShowTrivia extends ShowBoundModel {

    @Lob
    @Column(nullable = false)
    private String trivia;

    public ShowTrivia(Show show, String trivia) {
        super(show);
        this.trivia = trivia;
    }

}
