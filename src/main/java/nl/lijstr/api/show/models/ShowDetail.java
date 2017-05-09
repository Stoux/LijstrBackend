package nl.lijstr.api.show.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.*;
import nl.lijstr.api.abs.base.models.ShortRating;
import nl.lijstr.domain.imdb.Genre;
import nl.lijstr.domain.imdb.SpokenLanguage;
import nl.lijstr.domain.shows.Show;
import nl.lijstr.domain.shows.ShowRating;

/**
 * A view model based on a {@link Show}.
 */
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShowDetail {

    private long id;
    private Long oldSiteId;

    private LocalDateTime created;
    private LocalDateTime lastUpdated;

    private String imdbId;
    private String title;
    private String originalTitle;
    private String dutchTitle;

    private Integer startYear;
    private Integer endYear;
    private LocalDate premiereDate;
    private Integer runtime;

    private Double imdbRating;
    private Long imdbVotes;
    private Integer metacriticScore;

    private String shortPlot;
    private String longPlot;

    private String ageRating;
    private boolean hasPoster;

    private String youtubeUrl;

    private Long addedBy;

    private List<ShowSeasonDetail> seasons;

    private List<Genre> genres;
    private List<SpokenLanguage> languages;

    private List<ShortRating> latestShowRatings;

    /**
     * Convert a {@link Show} to a {@link ShowDetail} object.
     *
     * @param show The show
     *
     * @return the detail object
     */
    public static ShowDetail fromShow(Show show) {
        ShowDetailBuilder builder = ShowDetail.builder()
            .id(show.getId())
            .oldSiteId(show.getOldSiteId())
            .created(show.getCreated())
            .lastUpdated(show.getLastUpdated())
            .imdbId(show.getImdbId())
            .title(show.getTitle())
            .originalTitle(show.getOriginalTitle())
            .dutchTitle(show.getDutchTitle())
            .startYear(show.getStartYear())
            .endYear(show.getEndYear())
            .premiereDate(show.getPremiereDate())
            .runtime(show.getRuntime())
            .imdbRating(show.getImdbRating())
            .imdbVotes(show.getImdbVotes())
            .metacriticScore(show.getMetacriticScore())
            .shortPlot(show.getShortPlot())
            .longPlot(show.getLongPlot())
            .ageRating(show.getAgeRating())
            .hasPoster(show.isPoster())
            .youtubeUrl(show.getYoutubeUrl())
            .genres(show.getGenres())
            .languages(show.getLanguages());

        //Add ratings
        List<ShowRating> ratings = show.getLatestShowRatings();
        if (ratings != null) {
            List<ShortRating> shortRatings = ratings.stream().map(ShortRating::new).collect(Collectors.toList());
            builder.latestShowRatings(shortRatings);
        }

        builder.seasons(show.getSeasons().stream().map(ShowSeasonDetail::fromSeason).collect(Collectors.toList()));

        if (show.getAddedBy() != null) {
            builder.addedBy(show.getAddedBy().getId());
        }

        return builder.build();
    }

}
