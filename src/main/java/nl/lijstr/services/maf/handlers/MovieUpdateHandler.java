package nl.lijstr.services.maf.handlers;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
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
import nl.lijstr.repositories.abs.BasicRepository;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.repositories.movies.people.MovieCharacterRepository;
import nl.lijstr.repositories.movies.people.MovieDirectorRepository;
import nl.lijstr.repositories.movies.people.MovieWriterRepository;
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
    private MovieWriterRepository movieWriterRepository;
    @Autowired
    private MovieDirectorRepository movieDirectorRepository;
    @Autowired
    private MovieCharacterRepository movieCharacterRepository;

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

        //Runtimes
        handler.compareAndModify(
                "runtime", movie.getRuntime(), apiMovie.getRuntime(),
                FieldConverters::convertRuntime, movie::setRuntime
        );

        //Plots
        handler.modify("shortPlot");
        handler.modify("longPlot");

        //Age rating
        handler.modify("ageRating");

        //Fetch poster
        updatePoster(movie, apiMovie);

        //Update last updated
        movie.setLastUpdated(LocalDateTime.now());
    }

    private void updateTitles(FieldModifyHandler handler, Movie movie, ApiMovie apiMovie) {
//        handler.modify("title");
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
//        if (movie.getOriginalTitle() != null) {
//            apiMovie.getAkas().stream()
//                    .filter(ApiAka::isFrench)
//                    .filter(aka -> movie.getTitle().equalsIgnoreCase(aka.getTitle()))
//                    .findFirst()
//                    .ifPresent(aka -> {
//                        //Title is french...
//                        logger.warn(
//                                "[{}] French title | Replacing '{}' with original title: '{}'",
//                                movie.getId(), movie.getTitle(), movie.getOriginalTitle()
//                        );
//
//                        //Override the title and add a changed value to the history
//                        historyRepository.saveAndFlush(new FieldHistory(
//                                FieldHistory.getDatabaseClassName(Movie.class),
//                                movie.getId(),
//                                "title",
//                                movie.getTitle(),
//                                movie.getOriginalTitle()
//                        ));
//                        movie.setTitle(movie.getOriginalTitle());
//                    });
//        }
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
                movieWriterRepository, movie.getWriters(), apiMovie.getWriters(), ApiPerson::getName,
                (p, a) -> new MovieWriter(movie, p), null
        );
    }

    private void updateDirectors(final Movie movie, ApiMovie apiMovie) {
        updateImdbPeople(
                movieDirectorRepository, movie.getDirectors(), apiMovie.getDirectors(), ApiPerson::getName,
                (p, a) -> new MovieDirector(movie, p), null
        );
    }

    private void updateActors(final Movie movie, ApiMovie apiMovie) {
        updateImdbPeople(
                movieCharacterRepository, movie.getCharacters(), apiMovie.getActors(), ApiActor::getActorName,
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
     * @param repository    The repository of the items (used for removing)
     * @param currentItems  The current list
     * @param newItems      The new items
     * @param getPersonName Get a person's name from a new item
     * @param createX       Create a new item
     * @param updateX       Ability to update X with the values from Y
     * @param <X>           The Movie element
     * @param <Y>           The API element
     * @param <R>           The Repository for the Movie Element
     */
    private <X extends ImdbIdentifiable, Y extends ImdbIdentifiable, R extends BasicRepository<X>> void updateImdbPeople(
            final R repository, final List<X> currentItems, Collection<Y> newItems, Function<Y, String> getPersonName,
            BiFunction<Person, Y, X> createX, BiConsumer<X, Y> updateX) {
        if (newItems == null) {
            // No items found in the API, which means we will remove them all (if there are any)
            // How in the heck are there movies with now writers by the way?
            newItems = Collections.emptyList();
        }

        // MAF has returned duplicate entries in the past (which we have copied). Remove them if present.
        removeDuplicateImdbPeople(repository, currentItems);

        // Map the existing items to their IMDB ID
        final Map<String, X> itemMap = Utils.toMap(currentItems, ImdbIdentifiable::getImdbId);

        // Keep track of any item IDs that we have already processed
        final Set<String> processedImdbIds = new HashSet<>();

        // Loop through new items to find ones that need to be updated or added
        newItems.forEach(newItem -> {
            final String newId = newItem.getImdbId();
            if (processedImdbIds.contains(newId)) {
                // Already handled an item with this ID, trash API is returning duplicates.
                return;
            } else {
                processedImdbIds.add(newId);
            }

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
        itemMap.values().forEach(x -> {
            // Remove it from the list linked with the movie
            currentItems.remove(x);
            // Remove it from the DB
            repository.delete(x);
        });
    }

    /**
     * Check the list for any duplicate IDs.
     *
     * MAF has returned duplicate people (as seperate entries) in the past. Clean those up.
     *
     * @param currentItems The already added items
     * @param <X> The IMDB item
     */
    private <X extends ImdbIdentifiable, R extends BasicRepository<X>> void removeDuplicateImdbPeople(
        final R repository, final List<X> currentItems
    ) {
        final Set<String> foundIds = new HashSet<>();
        final ListIterator<X> listIterator = currentItems.listIterator();
        while(listIterator.hasNext()) {
            final X imdbItem = listIterator.next();
            final String id = imdbItem.getImdbId();
            if (foundIds.contains(id)) {
                // Already found this ID, remove the item from the list
                listIterator.remove();

                // Completely remove it from the DB
                repository.delete(imdbItem);
            } else {
                foundIds.add(id);
            }
        }
    }

    private void updatePoster(Movie movie, ApiMovie apiMovie) {
        String apiPosterUrl = apiMovie.getPosterUrl();
        if (apiPosterUrl == null || apiPosterUrl.isEmpty()) {
            movie.setPoster(false);
            return;
        }

        try {
            //Open the file as Input stream
            URL posterUrl = new URL(apiPosterUrl);
            InputStream posterStream = posterUrl.openStream();

            //Copy the file to the location
            File imageFile = new File(imgFolderLocation + movie.getId() + ".jpg");
            OutputStream fileStream = new FileOutputStream(imageFile);
            FileCopyUtils.copy(posterStream, fileStream);

            logger.debug(
                    "[{}] Updated poster | Copied '{}' -> '{}'",
                    movie.getImdbId(), apiPosterUrl, imageFile.getAbsolutePath()
            );

            movie.setPoster(true);
            return;
        } catch (MalformedURLException e) {
            logger.warn("Invalid Poster URL: {}", apiPosterUrl, e);
        } catch (IOException e) {
            logger.warn("Failed to copy poster: {}", e.getMessage(), e);
        }

        movie.setPoster(false);
    }


}
