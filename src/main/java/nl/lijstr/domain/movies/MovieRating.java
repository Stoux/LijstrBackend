package nl.lijstr.domain.movies;

import javax.persistence.Column;
import javax.persistence.Entity;
import lombok.*;
import nl.lijstr.domain.base.IdCmUserMovieModel;

/**
 * Created by Stoux on 03/12/2015.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MovieRating extends IdCmUserMovieModel {

    @Column(nullable = false)
    private boolean seen;
    @Column(precision = 2)
    private Double rating;
    private String comment;
    @Column(nullable = false)
    private boolean latest;

}
