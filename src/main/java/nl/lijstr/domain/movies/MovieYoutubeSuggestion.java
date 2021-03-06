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
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MovieYoutubeSuggestion extends IdCmUserMovieModel {

    private String youtubeUrl;
    @Column(length = 5000)
    private String comment;

}
