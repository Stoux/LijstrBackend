package nl.lijstr.api.users.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by Stoux on 19/04/2016.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionList {

    private List<String> permissions;

}
