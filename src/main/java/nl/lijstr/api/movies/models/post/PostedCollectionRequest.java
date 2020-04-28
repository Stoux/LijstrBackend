package nl.lijstr.api.movies.models.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Getter
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class PostedCollectionRequest {

    @NotEmpty
    private String title;
    private String description;
    private String keywords;
    private List<Long> movieIds;

}
