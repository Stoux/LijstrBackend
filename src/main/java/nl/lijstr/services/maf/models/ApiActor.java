package nl.lijstr.services.maf.models;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;
import nl.lijstr.domain.interfaces.ImdbIdentifiable;

/**
 * Created by Stoux on 03/12/2015.
 */
@ToString
@Getter
public class ApiActor implements ImdbIdentifiable {

    @SerializedName("actorId")
    private String imdbId;

    private String actorName;
    @SerializedName("urlPhoto")
    private String photoUrl;

    private String character;
    @SerializedName("urlCharacter")
    private String characterUrl;

    @SerializedName("main")
    private boolean mainCharacter;


}
