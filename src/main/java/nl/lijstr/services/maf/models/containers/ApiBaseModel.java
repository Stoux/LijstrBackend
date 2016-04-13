package nl.lijstr.services.maf.models.containers;

import lombok.*;
import nl.lijstr.services.maf.models.ApiAbout;

/**
 * Created by Stoux on 03/12/2015.
 */
public abstract class ApiBaseModel<X> {

    protected X data;

    @Getter
    private ApiAbout about;

}
