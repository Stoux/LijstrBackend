package nl.lijstr.domain.movies;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.lijstr.domain.base.IdCmUserMovieModel;
import nl.lijstr.domain.users.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

/**
 * Created by Stoux on 03/12/2015.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MovieComment extends IdCmUserMovieModel {

    @Column(nullable = false, length = 5000)
    private String comment;

    /**
     * Create a {@link MovieComment}.
     *
     * @param movie   The movie
     * @param user    The user who made the comment
     * @param comment The actual comment
     */
    public MovieComment(Movie movie, User user, String comment) {
        this.movie = movie;
        this.comment = comment;
        this.user = user;
    }

}
