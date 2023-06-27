package nl.lijstr.domain.movies;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.lijstr.domain.base.IdCmUserMovieModel;

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
public class MovieYoutubeSuggestion extends IdCmUserMovieModel {

    private String youtubeUrl;
    @Column(length = 5000)
    private String comment;

}
