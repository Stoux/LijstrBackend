package nl.lijstr.services.maf.handlers;

import java.util.Arrays;
import nl.lijstr.beans.ImdbBean;
import nl.lijstr.common.Utils;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.movies.MovieTrivia;
import nl.lijstr.domain.movies.people.MovieCharacter;
import nl.lijstr.domain.movies.people.MovieDirector;
import nl.lijstr.domain.movies.people.MovieWriter;
import nl.lijstr.exceptions.LijstrException;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.repositories.other.FieldHistoryRepository;
import nl.lijstr.repositories.other.FieldHistorySuggestionRepository;
import nl.lijstr.services.maf.handlers.util.FieldConverters;
import nl.lijstr.services.maf.handlers.util.FieldModifyHandler;
import nl.lijstr.services.maf.models.ApiActor;
import nl.lijstr.services.maf.models.ApiMovie;
import nl.lijstr.services.maf.models.ApiPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * MAF component for updating {@link Movie}s.
 */
@Component
public class MovieUpdateHandler extends TargetUpdateHandler<Movie, ApiMovie> {

    @Autowired
    public MovieUpdateHandler(MovieRepository targetRepository, FieldHistoryRepository historyRepository,
                              FieldHistorySuggestionRepository suggestionRepository, ImdbBean imdbBean,
                              @Value("${server.image-location.movies}") String imgFolderLocation) {
        super(targetRepository, historyRepository, suggestionRepository, imdbBean, imgFolderLocation);
    }

    @Override
    protected void updateBasics(FieldModifyHandler handler, Movie movie, ApiMovie apiMovie) {
        super.updateBasics(handler, movie, apiMovie);

        handler.compareAndModify("year", movie.getYear(), apiMovie.getYear(), FieldConverters::convertToYear,
            movie::setYear);
        handler.compareAndModify("released", movie.getReleased(), apiMovie.getReleaseDate(),
            FieldConverters::convertToDate, movie::setReleased);
    }

    @Override
    protected void updateTrivia(Movie movie, ApiMovie apiMovie) {
        Utils.updateList(movie.getTrivia(), apiMovie.getTrivia(), MovieTrivia::getTrivia,
            triviaFact -> new MovieTrivia(movie, triviaFact));
    }

    @Override
    protected void updatePersonnel(Movie movie, ApiMovie apiMovie) {
        updateImdbPeople(movie.getWriters(), apiMovie.getWriters(), ApiPerson::getName,
            (p, a) -> new MovieWriter(movie, p), null);

        updateImdbPeople(movie.getDirectors(), apiMovie.getDirectors(), ApiPerson::getName,
            (p, a) -> new MovieDirector(movie, p), null);

        updateImdbPeople(movie.getCharacters(), apiMovie.getActors(), ApiActor::getActorName,
            (p, a) -> new MovieCharacter(movie, p, a.getCharacter(), a.getCharacterUrl(), a.getPhotoUrl(),
                a.isMainCharacter()), this::checkActor);
    }

    @Override
    protected Movie updateOtherRelations(Movie movie, ApiMovie apiMovie) {
        return movie; //Has no other relations
    }
}
