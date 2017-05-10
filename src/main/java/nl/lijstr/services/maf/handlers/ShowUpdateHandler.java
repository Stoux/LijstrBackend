package nl.lijstr.services.maf.handlers;

import nl.lijstr.beans.ImdbBean;
import nl.lijstr.common.Utils;
import nl.lijstr.domain.shows.Show;
import nl.lijstr.domain.shows.ShowTrivia;
import nl.lijstr.domain.shows.people.ShowCharacter;
import nl.lijstr.domain.shows.people.ShowDirector;
import nl.lijstr.domain.shows.people.ShowWriter;
import nl.lijstr.repositories.other.FieldHistoryRepository;
import nl.lijstr.repositories.other.FieldHistorySuggestionRepository;
import nl.lijstr.repositories.shows.ShowRepository;
import nl.lijstr.services.maf.handlers.util.FieldConverters;
import nl.lijstr.services.maf.handlers.util.FieldModifyHandler;
import nl.lijstr.services.maf.models.ApiActor;
import nl.lijstr.services.maf.models.ApiPerson;
import nl.lijstr.services.maf.models.ApiShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * MAF component for updating {@link Show}s.
 */
@Component
public class ShowUpdateHandler extends TargetUpdateHandler<Show, ApiShow> {

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
        //TODO: Seasons & Episodes
    }
}
