package nl.lijstr.api.users.models;

import lombok.*;

/**
 * Created by Stoux on 18/04/2016.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshRequest {

    private String currentToken;

}
