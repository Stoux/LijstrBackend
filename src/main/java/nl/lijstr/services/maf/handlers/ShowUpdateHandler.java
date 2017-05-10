package nl.lijstr.services.maf.handlers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import nl.lijstr.beans.ImdbBean;
import nl.lijstr.common.Utils;
import nl.lijstr.domain.other.DateAccuracy;
import nl.lijstr.domain.shows.Show;
import nl.lijstr.domain.shows.ShowTrivia;
import nl.lijstr.domain.shows.episodes.ShowEpisode;
import nl.lijstr.domain.shows.people.ShowCharacter;
import nl.lijstr.domain.shows.people.ShowDirector;
import nl.lijstr.domain.shows.people.ShowWriter;
import nl.lijstr.domain.shows.seasons.ShowSeason;
import nl.lijstr.repositories.other.FieldHistoryRepository;
import nl.lijstr.repositories.other.FieldHistorySuggestionRepository;
import nl.lijstr.repositories.shows.ShowRepository;
import nl.lijstr.services.maf.handlers.util.FieldConverters;
import nl.lijstr.services.maf.handlers.util.FieldModifyHandler;
import nl.lijstr.services.maf.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * MAF component for updating {@link Show}s.
 */
@Component
public class ShowUpdateHandler extends TargetUpdateHandler<Show, ApiShow> {

    private static final DateTimeFormatter EPISODE_DATE_FORMATTER = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);
    //[3 letters of the month][A space][4 year numbers]
    private static final int MONTH_YEAR_LENGTH = 8;

    @Autowired
    public ShowUpdateHandler(ShowRepository targetRepository, FieldHistoryRepository historyRepository,
                             FieldHistorySuggestionRepository suggestionRepository, ImdbBean imdbBean,
                             @Value("${server.image-location.shows}") String imgFolderLocation) {
        super(targetRepository, historyRepository, suggestionRepository, imdbBean, imgFolderLocation);
    }

    @Override
    protected void updateBasics(FieldModifyHandler handler, Show show, ApiShow apiShow) {
        super.updateBasics(handler, show, apiShow);

        //Get year
        Integer startYear = null, endYear = null;
        String apiYear = apiShow.getYear();
        if (apiYear != null && apiYear.matches("\\d{4}-(\\d{4})?")) {
            startYear = FieldConverters.convertToYear(apiYear.substring(0, 4));
            if (apiYear.length() > 5) {
                endYear = FieldConverters.convertToYear(apiYear.substring(5));
            }
        }
        handler.compareAndModify("startYear", show.getStartYear(), startYear, i -> i, show::setStartYear);
        handler.compareAndModify("endYear", show.getEndYear(), endYear, i -> i, show::setEndYear);

        handler.compareAndModify("premiereDate", show.getPremiereDate(), apiShow.getReleaseDate(),
            FieldConverters::convertToDate, show::setPremiereDate);
    }

    @Override
    protected void updateTrivia(Show show, ApiShow apiShow) {
        Utils.updateList(show.getTrivia(), apiShow.getTrivia(), ShowTrivia::getTrivia,
            triviaFact -> new ShowTrivia(show, triviaFact));
    }

    @Override
    protected void updatePersonnel(Show show, ApiShow apiShow) {
        updateImdbPeople(show.getWriters(), apiShow.getWriters(), ApiPerson::getName, (p, a) -> new ShowWriter(show, p),
            null);

        updateImdbPeople(show.getDirectors(), apiShow.getDirectors(), ApiPerson::getName,
            (p, a) -> new ShowDirector(show, p), null);

        updateImdbPeople(show.getCharacters(), apiShow.getActors(), ApiActor::getActorName,
            (p, a) -> new ShowCharacter(show, p, a.getCharacter(), a.getCharacterUrl(), a.getPhotoUrl(),
                a.isMainCharacter()), this::checkActor);
    }

    @Override
    protected void updateOtherRelations(Show show, ApiShow apiShow) {
        List<ShowSeason> seasons = show.getSeasons();
        List<ApiSeason> apiSeasons = apiShow.getSeasons();

        for (ApiSeason apiSeason : apiSeasons) {
            //Find the matching ShowSeason
            Optional<ShowSeason> foundSeason = seasons.stream()
                .filter(s -> Objects.equals(s.getSeasonNumber(), apiSeason.getSeasonNumber()))
                .findFirst();

            if (!foundSeason.isPresent()) {
                seasons.add(new ShowSeason(show, apiSeason.getSeasonNumber()));
            }

            ShowSeason showSeason = foundSeason.orElseGet(() -> {
                ShowSeason newSeason = new ShowSeason(show, apiSeason.getSeasonNumber());
                seasons.add(newSeason);
                return newSeason;
            });

            updateSeason(showSeason, apiSeason);
        }
    }

    private void updateSeason(ShowSeason showSeason, ApiSeason apiSeason) {
        for (ApiEpisode apiEpisode : apiSeason.getEpisodes()) {
            //Find the matching ShowEpisode
            List<ShowEpisode> showEpisodes = showSeason.getEpisodes();
            Optional<ShowEpisode> foundEpisode = showEpisodes.stream()
                .filter(e -> Objects.equals(e.getEpisodeNumber(), apiEpisode.getEpisodeNumber()))
                .findFirst();

            ShowEpisode showEpisode = foundEpisode.orElseGet(() -> {
                ShowEpisode newEpisode =
                    new ShowEpisode(apiEpisode.getImdbId(), showSeason, apiEpisode.getEpisodeNumber());
                showEpisodes.add(newEpisode);
                return newEpisode;
            });

            //Update the episode
            updateEpisode(showEpisode, apiEpisode);
        }
    }


    private void updateEpisode(ShowEpisode showEpisode, ApiEpisode apiEpisode) {
        if (showEpisode.getTitle() == null && apiEpisode.hasValidTitle()) {
            showEpisode.setTitle(apiEpisode.getTitle());
        }

        if (apiEpisode.hasValidPlot()) {
            FieldModifyHandler handler = new FieldModifyHandler(showEpisode, apiEpisode, historyRepository, suggestionRepository);
            handler.modify("plot");
        }

        if (!StringUtils.isEmpty(apiEpisode.getDate()) && showEpisode.getAirDateAccuracy() != DateAccuracy.TIME) {
            String date = apiEpisode.getDate();
            DateAccuracy accuracy;
            LocalDate episodeDate;
            if (date.matches("(\\d+ )\\w{3}?\\.? \\d{4}")) {
                date = date.replaceAll("\\.", ""); //Remove the . after the month (if present)
                if (date.length() == MONTH_YEAR_LENGTH) {
                    accuracy = DateAccuracy.MONTH;
                    date = "1 " + date;
                } else {
                    accuracy = DateAccuracy.DAY;
                }

                episodeDate = LocalDate.parse(date, EPISODE_DATE_FORMATTER);
                if (accuracy == DateAccuracy.MONTH) {
                    //Use the end of the month for sorting purposes
                    episodeDate = episodeDate.plusMonths(1).minusDays(1); //Move to the end of the month
                }
            } else if (date.matches("\\d{4}")) {
                accuracy = DateAccuracy.YEAR;
                //Use the end of the year for sorting purposes
                episodeDate = LocalDate.of(Integer.parseInt(date), Month.DECEMBER, 31);
            } else {
                return;
            }


            LocalDateTime airTime = episodeDate.atTime(12, 0);
            showEpisode.setAirDate(airTime);
            showEpisode.setAirDateAccuracy(accuracy);
        }
    }

}
