package nl.lijstr.api.movies;

import nl.lijstr.api.abs.AbsService;
import nl.lijstr.api.movies.models.post.PostedCollectionRequest;
import nl.lijstr.common.Utils;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.movies.MovieCollection;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.repositories.movies.MovieCollectionRepository;
import nl.lijstr.repositories.movies.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/movies/collections", produces = "application/json")
public class MovieCollectionEndpoint extends AbsService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieCollectionRepository collectionRepository;

    @RequestMapping("/{id}")
    public MovieCollection getById(@PathVariable("id") final long id) {
        return findOne(collectionRepository, id, "movie collection");
    }

    /**
     * Get a map of all the people bound to this model.
     *
     * @param optQuery Optional filter
     *
     * @return the map with IDs to names
     */
    @RequestMapping(value = "/")
    public Map<Long, String> getAll(@RequestParam(name = "query", required = false) Optional<String> optQuery) {
        final List<MovieCollection> collections;
        if (optQuery.isPresent()) {
            final String query = optQuery.get();
            collections = collectionRepository.findByTitleContainingOrKeywordsContaining(query, query);
        } else {
            collections = collectionRepository.findAll();
        }

        return Utils.toMap(collections, MovieCollection::getId, MovieCollection::getTitle);
    }

    /**
     * Create a new MovieCollection
     *
     * @param postedRequest
     *
     * @return
     */
    @Secured(Permission.MOVIE_MOD)
    @Transactional
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public MovieCollection addCollection(@Valid @RequestBody PostedCollectionRequest postedRequest) {
        // Find the linked movies (if any)
        final List<Movie> linkedMovies = postedMovieIdsToMovies(postedRequest);

        // Create the collection
        final MovieCollection newCollection = new MovieCollection(
            postedRequest.getTitle(),
            postedRequest.getKeywords(),
            postedRequest.getDescription(),
            linkedMovies
        );

        return collectionRepository.save(newCollection);
    }

    @Secured(Permission.MOVIE_MOD)
    @Transactional
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public MovieCollection updateCollection(@PathVariable("id") final long id, @Valid @RequestBody PostedCollectionRequest postedRequest) {
        final MovieCollection collection = findOne(collectionRepository, id, "movie collection");

        collection.setTitle(postedRequest.getTitle());
        collection.setDescription(postedRequest.getDescription());
        collection.setKeywords(postedRequest.getKeywords());
        collection.setMovies(postedMovieIdsToMovies(postedRequest));

        return collectionRepository.save(collection);
    }

    private List<Movie> postedMovieIdsToMovies(PostedCollectionRequest postedRequest) {
        // Find the linked movies (if any)
        final List<Movie> linkedMovies;
        final List<Long> postedMovieIds = postedRequest.getMovieIds();
        if (postedMovieIds != null && !postedMovieIds.isEmpty()) {
            linkedMovies = postedMovieIds.stream().map(
                id -> findOne(movieRepository, id, "movie")
            ).collect(Collectors.toList());
        } else {
            linkedMovies = new ArrayList<>();
        }
        return linkedMovies;
    }


}
