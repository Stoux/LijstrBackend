package nl.lijstr.api.shows.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.*;
import nl.lijstr.api.abs.base.models.ShortRating;
import nl.lijstr.api.abs.base.models.TargetSummary;
import nl.lijstr.domain.shows.Show;
import nl.lijstr.domain.shows.ShowRating;

/**
 * A summarized version of a {@link Show}.
 */
@Getter
@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ShowSummary extends TargetSummary {

    @Builder
    private ShowSummary(long id, String imdbId, String title, Double imdbRating, Integer metacriticScore,
                       Map<Long, ShortRating> latestRatings) {
        super(id, imdbId, title, imdbRating, metacriticScore, latestRatings);
    }

    /**
     * Conver a {@link Show} to a {@link ShowSummary}.
     * @param show The show
     * @param requestUsers The requested users
     * @return the summary
     */
    public static ShowSummary convert(Show show, Set<Long> requestUsers) {
        ShowSummaryBuilder builder = ShowSummary.builder()
            .id(show.getId())
            .imdbId(show.getImdbId())
            .title(show.getTitle())
            .imdbRating(show.getImdbRating())
            .metacriticScore(show.getMetacriticScore());

        if (requestUsers == null || !requestUsers.isEmpty()) {
            Stream<ShowRating> ratingStream = show.getLatestShowRatings().stream();
            if (requestUsers != null) {
                ratingStream = ratingStream.filter(r -> requestUsers.contains(r.getUser().getId()));
            }
            Map<Long, ShortRating> shortRatings =
                ratingStream.map(ShortRating::new).collect(Collectors.toMap(ShortRating::getUser, o -> o));
            builder.latestRatings(shortRatings);
        }

        return builder.build();
    }

}
