package nl.lijstr.services.maf.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.*;

/**
 * Created by Stoux on 03/12/2015.
 */
@Getter
public class ApiShow extends ApiMovie {

    private List<ApiSeason> seasons;

}
