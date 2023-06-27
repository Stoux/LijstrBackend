package nl.lijstr.domain.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.lijstr.domain.base.IdModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

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

    @Column(unique = true)
    private String name;

    /**
     * Get the list of available permissions.
     *
     * @return the list
     */
    public static String[] list() {
        return new String[]{ADMIN, USER, MOVIE_MOD, MOVIE_USER};
    }

    @Override
    public String toString() {
        return name;
    }


}
