package nl.lijstr.services.migrate.migrators;

import java.util.*;
import nl.lijstr.common.Utils;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.services.maf.MafApiService;
import nl.lijstr.services.migrate.models.MigrationProgress;
import nl.lijstr.services.migrate.models.movies.OldMovie;
import nl.lijstr.services.migrate.retrofit.OldSiteService;
import org.apache.logging.log4j.Logger;
import retrofit2.Call;

/**
 * Migrator with the ability to migrate movies from the old site to the new one.
 */
public class MovieMigrator implements OldSiteMigrator {

    private final Logger logger;
    private final OldSiteService oldSiteService;
    private final MovieRepository movieRepository;
    private final MafApiService mafApiService;
    private final MigrationProgress currentProgress;

    public MovieMigrator(OldSiteService oldSiteService, MovieRepository movieRepository, MafApiService mafApiService,
                         MigrationProgress currentProgress, Logger logger) {
        this.logger = logger;
        this.oldSiteService = oldSiteService;
        this.movieRepository = movieRepository;
        this.mafApiService = mafApiService;
        this.currentProgress = currentProgress;
    }

    public void migrate() {
        try {
            logger.info("Starting movie migration.");
            final Map<String, OldMovie> oldMovieMap = getOldMovieMap();
            final Map<String, Movie> currentMovieMap = Utils.toMap(movieRepository.findAll(), Movie::getImdbId);

            final List<Movie> newMovies = new ArrayList<>();

            //Loop through old movies
            oldMovieMap.forEach((id, oldMovie) -> {
                //Check if it already exists
                Movie newMovie = updateExisting(id, oldMovie, currentMovieMap);
                if (newMovie != null) {
                    newMovies.add(newMovie);
                }
            });

            //Add new movies to the DB and fill them with data.
            newMovies.stream().map(this::add).forEach(this::fillWithData);

            currentProgress.finish();
        } catch (RuntimeException e) {
            logger.warn(
                    "Migration failed ({}): {}",
                    e.getClass().getSimpleName(),
                    e.getMessage()
            );
            this.currentProgress.fail(e);
        }
    }

    /**
     * Update an existing movie if it exists.
     * Returns a new movie if it doesn't exist.
     *
     * @param imdbId        The IMDB ID
     * @param oldMovie      The old movie
     * @param currentMovies The map of current movies (IMDB ID -> Movie)
     * @return A new movie or null if it already exists
     */
    private Movie updateExisting(String imdbId, OldMovie oldMovie, Map<String, Movie> currentMovies) {
        if (currentMovies.containsKey(imdbId)) {
            Movie currentMovie = currentMovies.get(imdbId);
            if (!Objects.equals(oldMovie.getId(), currentMovie.getOldSiteId())) {
                logger.info(
                        "Updated old site ID on movie '{}' ({}) to {}",
                        currentMovie.getTitle(),
                        currentMovie.getImdbId(),
                        oldMovie.getId()
                );
                currentMovie.setOldSiteId(oldMovie.getId());
                movieRepository.save(currentMovie);
                currentProgress.updated(currentMovie.getImdbId(), currentMovie.getTitle());
            }
            return null;
        } else {
            logger.info(
                    "Adding new movie: {} ({}) with old id: {}",
                    oldMovie.getTitle(),
                    imdbId,
                    oldMovie.getId()
            );
            return new Movie(imdbId, oldMovie.getTitle(), oldMovie.getId());
        }
    }

    private Movie add(final Movie movie) {
        logger.debug("Adding movie to DB: {}", movie.getImdbId());
        Movie saved = this.movieRepository.save(movie);
        this.currentProgress.added(movie.getImdbId(), movie.getTitle());
        return saved;
    }

    private Movie fillWithData(final Movie movie) {
        logger.info("Filling movie with data: {} ({})", movie.getId(), movie.getImdbId());
        Movie saved = this.mafApiService.updateMovie(movie);
        this.currentProgress.filled(saved.getImdbId());

        //Wait a bit before calling the one.
        try {
            Thread.sleep(750);
        } catch (InterruptedException e) {
            logger.warn("Failed to sleep. Shouldn't be called.");
        }

        return saved;
    }


    private Map<String, OldMovie> getOldMovieMap() {
        Call<Map<Long, OldMovie>> listCall = oldSiteService.listMovies();
        Map<Long, OldMovie> movies = Utils.executeCall(listCall);
        Map<String, OldMovie> map = new HashMap<>();
        for (OldMovie movie : movies.values()) {
            String id = getImdbId(movie.getImdbLink());
            map.put(id, movie);
        }
        return map;
    }

}
