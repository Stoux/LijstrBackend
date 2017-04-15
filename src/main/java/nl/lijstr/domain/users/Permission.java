package nl.lijstr.domain.users;

import javax.persistence.Column;
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
    public static final String USER = "ROLE_USER";

    public static final String MOVIE_MOD = "ROLE_MOVIE_MOD";
    public static final String MOVIE_USER = "ROLE_MOVIE";

    public static final String SHOW_MOD = "ROLE_SHOW_MOD";
    public static final String SHOW_USER = "ROLE_SHOW";

    @Column(unique = true)
    private String name;

    /**
     * Get the list of available permissions.
     *
     * @return the list
     */
    public static String[] list() {
        return new String[]{ADMIN, USER, MOVIE_MOD, MOVIE_USER, SHOW_MOD, SHOW_USER};
    }

    @Override
    public String toString() {
        return name;
    }


}
