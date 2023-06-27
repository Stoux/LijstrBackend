package nl.lijstr.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

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
