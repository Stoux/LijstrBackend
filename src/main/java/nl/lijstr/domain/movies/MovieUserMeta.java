package nl.lijstr.domain.movies;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.lijstr.domain.base.IdCmUserMovieModel;
import nl.lijstr.domain.users.User;

import jakarta.persistence.Entity;

/**
 * Created by Stoux on 03/12/2015.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MovieUserMeta extends IdCmUserMovieModel {

    private boolean wantToWatch;

    /**
     * Create a new MovieUserMeta object.
     *
     * @param user        The user
     * @param movie       The movie
     * @param wantToWatch Wants to watch the movie
     */
    public MovieUserMeta(User user, Movie movie, boolean wantToWatch) {
        this.user = user;
        this.movie = movie;
        this.wantToWatch = wantToWatch;
    }

}
