package nl.lijstr.services.omdb.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OmdbSearchContainer {

    @SerializedName("Search")
    private List<OmdbSearchResultObject> results;
    @SerializedName("totalResults")
    private String totalResults;
    @SerializedName("Response")
    private String response;

}
