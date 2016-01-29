package nl.lijstr.domain.base;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import lombok.*;
import nl.lijstr.domain.movies.Movie;

/**
 * A IdCmUserMovieModel that is linked to a Movie.
 */
@Getter
@Setter
@MappedSuperclass
public abstract class IdCmUserMovieModel extends IdCmUserModel {

    @ManyToOne
    private Movie movie;


}
