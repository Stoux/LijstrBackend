package nl.lijstr.api.abs.base;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import nl.lijstr.api.abs.AbsService;
import nl.lijstr.api.abs.base.models.post.PostedRequest;
import nl.lijstr.domain.base.IdModel;
import nl.lijstr.domain.interfaces.Target;
import nl.lijstr.exceptions.BadRequestException;
import nl.lijstr.repositories.abs.BasicTargetRepository;

/**
 * Created by Stoux on 09/05/2017.
 */
public abstract class TargetEndpoint<T extends IdModel & Target, X extends BasicTargetRepository<T>> extends AbsService {

    protected static final String DETAIL_PATH = "/{id}";
    protected static final String ORIGINAL_PATH = "/{id}/original";

    protected final X targetRepository;
    protected final String itemName;

    public TargetEndpoint(X targetRepository, String itemName) {
        this.targetRepository = targetRepository;
        this.itemName = itemName;
    }

    /**
     * Get all items as summary items.
     *
     * @param requestedUsers The requested users (ratings)
     * @param convertMethod  Method to convert an item to it's summary model.
     * @param <S>            The summary class
     *
     * @return the list of summaries
     */
    protected <S> List<S> summaryList(String requestedUsers, BiFunction<T, Set<Long>, S> convertMethod) {
        final Set<Long> users = parseUsers(requestedUsers);
        return targetRepository.findAllByOrderByTitleAsc()
            .stream()
            .map(i -> convertMethod.apply(i, users))
            .collect(Collectors.toList());
    }

    /**
     * Check if an item isn't already added.
     *
     * @param request the request
     */
    protected void checkIfNotAdded(PostedRequest request) {
        if (targetRepository.findByImdbId(request.getImdbId()) != null) {
            throw new BadRequestException(itemName + " already added");
        }
    }

    @SuppressWarnings("squid:S1168")
    private static Set<Long> parseUsers(final String requestedUsers) {
        if (requestedUsers == null) {
            //Explicitly return null instead of an empty array as an empty array means return everything available
            return null;
        }

        if (requestedUsers.length() == 0) {
            return Collections.emptySet();
        }

        if (!requestedUsers.matches("^(\\d+)(,\\d+)*$")) {
            throw new BadRequestException("Invalid user list");
        }

        String[] split = requestedUsers.split(",");
        return Arrays.stream(split)
            .map(Long::parseLong)
            .collect(Collectors.toSet());
    }

}
