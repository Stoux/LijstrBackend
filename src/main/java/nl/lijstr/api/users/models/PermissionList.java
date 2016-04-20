package nl.lijstr.api.users.models;

import java.util.List;
import lombok.*;

/**
 * Created by Stoux on 19/04/2016.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionList {

    private List<String> permissions;

}
