package nl.lijstr.services.maf.models.containers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.lijstr.services.maf.models.ApiMovie;

import java.util.List;

/**
 * Created by Stoux on 03/12/2015.
 */
@NoArgsConstructor
public class ApiMovieModel extends ApiBaseModel<ApiMovieModel.MoviesHolder> {

    /**
     * Get the Movie.
     *
     * @return The model
     */
    public ApiMovie getMovie() {
        if (data != null) {
            return data.getMovies().get(0);
        } else {
            return null;
        }
    }

    /**
     * Subclass that contains the list of movies.
     */
    public static class MoviesHolder {
        @Getter
        private List<ApiMovie> movies;
    }

}
