package nl.lijstr.common;

import java.util.List;
import lombok.*;

/**
 * A container for a paged result.
 */
@Getter
@AllArgsConstructor
public class PageResult<X> {

    private int page;
    private int size;
    private long totalElements;
    private long totalPages;

    private List<X> result;

}
