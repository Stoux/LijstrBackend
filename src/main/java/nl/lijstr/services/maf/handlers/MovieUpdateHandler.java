package nl.lijstr.services.maf.handlers;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import nl.lijstr.beans.ImdbBean;
import nl.lijstr.common.Utils;
import nl.lijstr.domain.imdb.Genre;
import nl.lijstr.domain.imdb.Person;
import nl.lijstr.domain.imdb.SpokenLanguage;
import nl.lijstr.domain.interfaces.ImdbIdentifiable;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.movies.MovieTrivia;
import nl.lijstr.domain.movies.people.MovieCharacter;
import nl.lijstr.domain.movies.people.MovieDirector;
import nl.lijstr.domain.movies.people.MovieWriter;
import nl.lijstr.domain.other.FieldHistory;
import nl.lijstr.exceptions.LijstrException;
import nl.lijstr.processors.annotations.InjectLogger;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.repositories.other.FieldHistoryRepository;
import nl.lijstr.repositories.other.FieldHistorySuggestionRepository;
import nl.lijstr.services.maf.handlers.util.FieldConverters;
import nl.lijstr.services.maf.handlers.util.FieldModifyHandler;
import nl.lijstr.services.maf.models.ApiActor;
import nl.lijstr.services.maf.models.ApiAka;
import nl.lijstr.services.maf.models.ApiMovie;
import nl.lijstr.services.maf.models.ApiPerson;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

/**
 * Created by Stoux on 29/01/2016.
 */
@Component
public class MovieUpdateHandler {

    @InjectLogger
    private Logger logger;

    //Autowired repos
    @Autowired
    private FieldHistoryRepository historyRepository;
    @Autowired
    private FieldHistorySuggestionRepository suggestionRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ImdbBean imdbBean;

    @Value("${server.image-location}")
    private String imgFolderLocation;

    /**
     * Update a movie using the data from an API.
     *
     * @param movie    The movie
     * @param apiMovie The API mapped movie
     *
     * @return the updated movie
     */
    public Movie update(Movie movie, ApiMovie apiMovie) {
        //Check if save IMDB id
        if (!movie.getImdbId().equals(apiMovie.getImdbId())) {
            throw new LijstrException("IMDB IDs are not equal");
        }

        //Movie
        updateMovie(movie, apiMovie);

        //IMDB stuff
        updateTrivia(movie, apiMovie);
        updateLanguages(movie, apiMovie);
        updateGenres(movie, apiMovie);

        //Check people
        updateWriters(movie, apiMovie);
        updateDirectors(movie, apiMovie);
        updateActors(movie, apiMovie);

        return movieRepository.saveAndFlush(movie);
    }

    private void updateMovie(Movie movie, ApiMovie apiMovie) {
        FieldModifyHandler handler =
                new FieldModifyHandler(movie, apiMovie, historyRepository, suggestionRepository);

        //General info
        updateTitles(handler, movie, apiMovie);
        handler.compareAndModify(
                "year", movie.getYear(), apiMovie.getYear(),
                FieldConverters::convertToYear, movie::setYear
        );
        handler.compareAndModify(
                "released", movie.getReleased(), apiMovie.getReleaseDate(),
                FieldConverters::convertToDate, movie::setReleased
        );

        //Ratings & Votes
        handler.compareAndModify(
                "imdbRating", movie.getImdbRating(), apiMovie.getRating(),
                FieldConverters::convertToDouble, movie::setImdbRating
        );
        handler.compareAndModify(
                "imdbVotes", movie.getImdbVotes(), apiMovie.getNrOfVotes(),
                FieldConverters::convertToLong, movie::setImdbVotes
        );
        handler.compareAndModify(
                "metacriticScore", movie.getMetacriticScore(), apiMovie.getMetascore(),
                FieldConverters::convertMetaCriticScore, movie::setMetacriticScore
        );

        //Plots
        handler.modify("shortPlot");
        handler.modify("longPlot");

        //Age rating
        handler.modify("ageRating");

        //TODO: Runtimes

        //Fetch poster
        updatePoster(movie, apiMovie);

        //Update last updated
        movie.setLastUpdated(LocalDateTime.now());
    }

    private void updateTitles(FieldModifyHandler handler, Movie movie, ApiMovie apiMovie) {
        handler.modify("title");
        handler.modify("originalTitle");

        //Find the dutch title if there's one
        apiMovie.getAkas().stream()
                .filter(ApiAka::isDutch)
                .findFirst()
                .ifPresent(aka -> {
                    handler.compareAndModify(
                            "dutchTitle", movie.getDutchTitle(), aka.getTitle(),
                            s -> s, movie::setDutchTitle
                    );
                });

        //NOTE: Due to MyApiFilms fucking up & returning french titles we have to do some extra logic...
        if (movie.getOriginalTitle() != null) {
            apiMovie.getAkas().stream()
                    .filter(ApiAka::isFrench)
                    .filter(aka -> movie.getTitle().equalsIgnoreCase(aka.getTitle()))
                    .findFirst()
                    .ifPresent(aka -> {
                        //Title is french...
                        logger.warn(
                                "[{}] French title | Replacing '{}' with original title: '{}'",
                                movie.getId(), movie.getTitle(), movie.getOriginalTitle()
                        );

                        //Override the title and add a changed value to the history
                        historyRepository.saveAndFlush(new FieldHistory(
                                FieldHistory.getDatabaseClassName(Movie.class),
                                movie.getId(),
                                "title",
                                movie.getTitle(),
                                movie.getOriginalTitle()
                        ));
                        movie.setTitle(movie.getOriginalTitle());
                    });
        }
    }


    private void updateTrivia(Movie movie, ApiMovie apiMovie) {
        Utils.updateList(
                movie.getTrivia(), apiMovie.getTrivia(), MovieTrivia::getTrivia,
                triviaFact -> new MovieTrivia(movie, triviaFact)
        );
    }

    private void updateGenres(Movie movie, ApiMovie apiMovie) {
        Utils.updateList(
                movie.getGenres(), apiMovie.getGenres(), Genre::getGenre,
                imdbBean::getOrCreateGenre
        );
    }

    private void updateLanguages(Movie movie, ApiMovie apiMovie) {
        Utils.updateList(
                movie.getLanguages(), apiMovie.getLanguages(), SpokenLanguage::getLanguage,
                imdbBean::getOrCreateLanguage
        );
    }

    private void updateWriters(final Movie movie, ApiMovie apiMovie) {
        updateImdbPeople(
                movie.getWriters(), apiMovie.getWriters(), ApiPerson::getName,
                (p, a) -> new MovieWriter(movie, p), null
        );
    }

    private void updateDirectors(final Movie movie, ApiMovie apiMovie) {
        updateImdbPeople(
                movie.getDirectors(), apiMovie.getDirectors(), ApiPerson::getName,
                (p, a) -> new MovieDirector(movie, p), null
        );
    }

    private void updateActors(final Movie movie, ApiMovie apiMovie) {
        updateImdbPeople(
                movie.getCharacters(), apiMovie.getActors(), ApiActor::getActorName,
                (p, a) -> new MovieCharacter(
                        movie, p, a.getCharacter(), a.getCharacterUrl(),
                        a.getPhotoUrl(), a.isMainCharacter()
                ), this::checkActor
        );
    }

    private void checkActor(MovieCharacter actor, ApiActor apiActor) {
        FieldModifyHandler handler = new FieldModifyHandler(actor, apiActor, historyRepository, suggestionRepository);
        Arrays.asList("photoUrl", "character", "characterUrl", "mainCharacter")
                .forEach(handler::modify);
    }

    /**
     * Update the list of IMDB people connected to this movie.
     * <p>
     * This function allows for the conversion from Api elements to Movie elements.
     * It checks for new ones (and adds them), updates current ones and removes old ones.
     *
     * @param currentItems  The current list
     * @param newItems      The new items
     * @param getPersonName Get a person's name from a new item
     * @param createX       Create a new item
     * @param updateX       Ability to update X with the values from Y
     * @param <X>           The Movie element
     * @param <Y>           The API element
     */
    private <X extends ImdbIdentifiable, Y extends ImdbIdentifiable> void updateImdbPeople(
            final List<X> currentItems, Collection<Y> newItems, Function<Y, String> getPersonName,
            BiFunction<Person, Y, X> createX, BiConsumer<X, Y> updateX) {
        final Map<String, X> itemMap = Utils.toMap(currentItems, ImdbIdentifiable::getImdbId);

        //Loop through new items
        newItems.forEach(newItem -> {
            String newId = newItem.getImdbId();
            if (itemMap.containsKey(newId)) {
                X matchedItem = itemMap.remove(newId);
                if (updateX != null) {
                    updateX.accept(matchedItem, newItem);
                }
            } else {
                //Get the person
                Person person = imdbBean.getPerson(newId);
                if (person == null) {
                    person = imdbBean.addPerson(new Person(newId, getPersonName.apply(newItem)));
                }

                //Add as X
                currentItems.add(createX.apply(person, newItem));
            }
        });

        //Delete old ones
        itemMap.values().forEach(currentItems::remove);
    }

    private void updatePoster(Movie movie, ApiMovie apiMovie) {
        if (apiMovie.getPosterUrl().isEmpty()) {
            movie.setPoster(false);
            return;
        }

        try {
            //Open the file as Input stream
            URL posterUrl = new URL(apiMovie.getPosterUrl());
            InputStream posterStream = posterUrl.openStream();

            //Copy the file to the location
            File imageFile = new File(imgFolderLocation + movie.getId() + ".jpg");
            OutputStream fileStream = new FileOutputStream(imageFile);
            FileCopyUtils.copy(posterStream, fileStream);

            logger.debug(
                    "[{}] Updated poster | Copied '{}' -> '{}'",
                    movie.getImdbId(), apiMovie.getPosterUrl(), imageFile.getAbsolutePath()
            );

            movie.setPoster(true);
            return;
        } catch (MalformedURLException e) {
            logger.warn("Invalid Poster URL: {}", apiMovie.getPosterUrl(), e);
        } catch (IOException e) {
            logger.warn("Failed to copy poster: {}", e.getMessage(), e);
        }

        movie.setPoster(false);
    }


}
