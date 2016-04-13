package nl.lijstr.domain.movies;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
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

    @JsonIgnore
    @ManyToOne
    private Movie movie;

    @Lob
    @Column(nullable = false)
    private String trivia;

}
