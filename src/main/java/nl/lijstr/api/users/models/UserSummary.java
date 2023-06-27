package nl.lijstr.api.users.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nl.lijstr.domain.users.User;

/**
 * Created by Stoux on 2-2-2017.
 */
@Getter
@AllArgsConstructor
public class UserSummary {

    private Long id;
    private String displayName;

    public static UserSummary convert(User user) {
        return new UserSummary(user.getId(), user.getDisplayName());
    }

}
