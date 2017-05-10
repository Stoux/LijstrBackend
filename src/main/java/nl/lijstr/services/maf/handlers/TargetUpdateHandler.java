package nl.lijstr.services.maf.handlers;

import io.jsonwebtoken.lang.Objects;
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
import nl.lijstr.domain.imdb.AbstractActor;
import nl.lijstr.domain.imdb.Genre;
import nl.lijstr.domain.imdb.Person;
import nl.lijstr.domain.imdb.SpokenLanguage;
import nl.lijstr.domain.interfaces.ImdbIdentifiable;
import nl.lijstr.domain.interfaces.Target;
import nl.lijstr.exceptions.LijstrException;
import nl.lijstr.processors.annotations.InjectLogger;
import nl.lijstr.repositories.abs.BasicRepository;
import nl.lijstr.repositories.other.FieldHistoryRepository;
import nl.lijstr.repositories.other.FieldHistorySuggestionRepository;
import nl.lijstr.services.maf.handlers.util.FieldConverters;
import nl.lijstr.services.maf.handlers.util.FieldModifyHandler;
import nl.lijstr.services.maf.models.ApiActor;
import nl.lijstr.services.maf.models.ApiAka;
import nl.lijstr.services.maf.models.ApiMovie;
import org.apache.logging.log4j.Logger;
import org.springframework.util.FileCopyUtils;

/**
 * Abstract update handler with fields that all subclasses of {@link Target} have.
 *
 * @param <DomainModel> The domain model (as used in the DB)
 * @param <ApiModel>    The API model (as used by MAF)
 */
public abstract class TargetUpdateHandler<DomainModel extends Target, ApiModel extends ApiMovie> {

    private static final List<String> CHARACTER_FIELDS =
        Arrays.asList("photoUrl", "character", "characterUrl", "mainCharacter");

    @InjectLogger
    protected Logger logger;

    protected final FieldHistoryRepository historyRepository;
    protected final FieldHistorySuggestionRepository suggestionRepository;
    private final BasicRepository<DomainModel> targetRepository;
    private final ImdbBean imdbBean;
    private final String imgFolderLocation;

    TargetUpdateHandler(BasicRepository<DomainModel> targetRepository, FieldHistoryRepository historyRepository,
                        FieldHistorySuggestionRepository suggestionRepository, ImdbBean imdbBean,
                        String imgFolderLocation) {
        this.historyRepository = historyRepository;
        this.suggestionRepository = suggestionRepository;
        this.targetRepository = targetRepository;
        this.imdbBean = imdbBean;
        this.imgFolderLocation = imgFolderLocation;
    }

    /**
     * Update a target using the data from the API.
     *
     * @param domainModel   The target
     * @param apiModel The API model
     *
     * @return the updated target
     */
    public DomainModel update(DomainModel domainModel, ApiModel apiModel) {
        if (!Objects.nullSafeEquals(domainModel.getImdbId(), apiModel.getImdbId())) {
            throw new LijstrException("IMDB IDs are not equal");
        }

        FieldModifyHandler handler = new FieldModifyHandler(domainModel, apiModel, historyRepository, suggestionRepository);
        updateBasics(handler, domainModel, apiModel);

        //IMDB data
        Utils.updateList(domainModel.getGenres(), apiModel.getGenres(), Genre::getGenre, imdbBean::getOrCreateGenre);
        Utils.updateList(domainModel.getLanguages(), apiModel.getLanguages(), SpokenLanguage::getLanguage,
            imdbBean::getOrCreateLanguage);

        updateTrivia(domainModel, apiModel);
        updatePersonnel(domainModel, apiModel);

        return targetRepository.saveAndFlush(domainModel);
    }

    protected void updateBasics(FieldModifyHandler handler, DomainModel domainModel, ApiModel apiModel) {
        //NOTE: Due to MyApiFilms fucking up they are returning french titles. No longer use them as title source.
        //handler.modify("title");
        handler.modify("originalTitle");

        //Find the dutch title if there's one
        apiModel.getAkas().stream().filter(ApiAka::isDutch).findFirst().ifPresent(aka -> {
            handler.compareAndModify("dutchTitle", domainModel.getDutchTitle(), aka.getTitle(), s -> s,
                domainModel::setDutchTitle);
        });

        //Ratings & Votes
        handler.compareAndModify("imdbRating", domainModel.getImdbRating(), apiModel.getRating(),
            FieldConverters::convertToDouble, domainModel::setImdbRating);
        handler.compareAndModify("imdbVotes", domainModel.getImdbVotes(), apiModel.getNrOfVotes(),
            FieldConverters::convertToLong, domainModel::setImdbVotes);
        handler.compareAndModify("metacriticScore", domainModel.getMetacriticScore(), apiModel.getMetascore(),
            FieldConverters::convertMetaCriticScore, domainModel::setMetacriticScore);

        //Runtime
        handler.compareAndModify("runtime", domainModel.getRuntime(), apiModel.getRuntime(), FieldConverters::convertRuntime,
            domainModel::setRuntime);

        //Plot
        handler.modify("shortPlot");
        handler.modify("longPlot");

        //Age rating
        handler.modify("ageRating");

        //Fetch poster
        updatePoster(domainModel, apiModel);

        //Update last updated
        domainModel.setLastUpdated(LocalDateTime.now());
    }

    protected abstract void updateTrivia(DomainModel domainModel, ApiModel apiModel);

    protected abstract void updatePersonnel(DomainModel domainModel, ApiModel apiModel);

    protected abstract void updateOtherRelations(DomainModel domainModel, ApiModel apiModel);

    /**
     * Update the list of IMDB people connected to this target.
     * <p>
     * This function allows for the conversion from API elements to Domain elements.
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
    protected <X extends ImdbIdentifiable, Y extends ImdbIdentifiable> void updateImdbPeople(final List<X> currentItems,
                                                                                   Collection<Y> newItems,
                                                                                   Function<Y, String> getPersonName,
                                                                                   BiFunction<Person, Y, X> createX,
                                                                                   BiConsumer<X, Y> updateX) {
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
        currentItems.removeAll(itemMap.values());
    }

    protected <X extends AbstractActor> void checkActor(X actor, ApiActor apiActor) {
        FieldModifyHandler handler = new FieldModifyHandler(actor, apiActor, historyRepository, suggestionRepository);
        CHARACTER_FIELDS.forEach(handler::modify);
    }

    private void updatePoster(DomainModel target, ApiModel apiModel) {
        String apiPosterUrl = apiModel.getPosterUrl();
        if (apiPosterUrl == null || apiPosterUrl.isEmpty()) {
            target.setPoster(false);
            return;
        }

        try {
            //Open the file as Input stream
            URL posterUrl = new URL(apiPosterUrl);
            InputStream posterStream = posterUrl.openStream();

            //Copy the file to the location
            File imageFile = new File(imgFolderLocation + target.getId() + ".jpg");
            OutputStream fileStream = new FileOutputStream(imageFile);
            FileCopyUtils.copy(posterStream, fileStream);

            logger.debug("[{}] Updated poster | Copied '{}' -> '{}'", target.getImdbId(), apiPosterUrl,
                imageFile.getAbsolutePath());

            target.setPoster(true);
            return;
        } catch (MalformedURLException e) {
            logger.warn("Invalid Poster URL: {}", apiPosterUrl, e);
        } catch (IOException e) {
            logger.warn("Failed to copy poster: {}", e.getMessage(), e);
        }

        target.setPoster(false);
    }

}
