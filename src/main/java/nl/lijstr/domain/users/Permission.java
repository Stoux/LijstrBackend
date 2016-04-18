package nl.lijstr.domain.users;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.lijstr.domain.base.IdModel;

/**
 * Created by Leon Stam on 18-4-2016.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Permission extends IdModel {

    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    public static final String ROLE_MOVIE = "ROLE_MOVIE";
    public static final String ROLE_MOVIE_MOD = "ROLE_MOVIE_MOD";

    private String permission;

    @Override
    public String toString() {
        return permission;
    }
}
