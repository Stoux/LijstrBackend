package nl.lijstr.domain.base;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import nl.lijstr.domain.movies.Movie;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

/**
 * A IdCmUserMovieModel that is linked to a Movie.
 */
@Getter
@Setter
@MappedSuperclass
public abstract class IdCmUserMovieModel extends IdCmUserModel {

    @JsonBackReference
    @ManyToOne
    protected Movie movie;

}
