package nl.lijstr.services.maf.models;

import com.google.gson.annotations.SerializedName;
import lombok.*;
import nl.lijstr.domain.interfaces.ImdbIdentifiable;

/**
 * Created by Stoux on 03/12/2015.
 */
@ToString
@Getter
public class ApiPerson implements ImdbIdentifiable {

    @SerializedName("titleName")
    private String name;
    private String imdbId;

}
