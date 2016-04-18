package nl.lijstr.domain.users;

import javax.persistence.Entity;
import lombok.*;
import nl.lijstr.domain.base.IdModel;

/**
 * Created by Leon Stam on 18-4-2016.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Permission extends IdModel {

    public static final String ADMIN = "ROLE_ADMIN";
    public static final String MOVIE = "ROLE_MOVIE";
    public static final String MOVIE_MOD = "ROLE_MOVIE_MOD";

    private String permission;

    @Override
    public String toString() {
        return permission;
    }
}
