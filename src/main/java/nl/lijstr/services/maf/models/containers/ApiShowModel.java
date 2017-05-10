package nl.lijstr.services.maf.models.containers;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.*;
import nl.lijstr.services.maf.models.ApiMovie;
import nl.lijstr.services.maf.models.ApiShow;

/**
 * Created by Stoux on 03/12/2015.
 */
@NoArgsConstructor
public class ApiShowModel extends ApiBaseModel<ApiShowModel.Holder> {

    /**
     * Get the Movie.
     *
     * @return The model
     */
    public ApiShow getShow() {
        if (data != null) {
            return data.getShows().get(0);
        } else {
            return null;
        }
    }

    /**
     * Subclass that contains the list of shows.
     */
    public static class Holder {

        @SerializedName("movies")
        @Getter
        private List<ApiShow> shows;
    }

}
