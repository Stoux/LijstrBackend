package nl.lijstr.services.maf.models.containers;

import lombok.Getter;
import nl.lijstr.services.maf.models.ApiAbout;

/**
 * The base class for any ApiCall.
 * <p>
 * Contains the data and about (version, etc).
 *
 * @param <X> The data class
 */
public abstract class ApiBaseModel<X> {

    protected X data;

    @Getter
    private ApiAbout about;

}
