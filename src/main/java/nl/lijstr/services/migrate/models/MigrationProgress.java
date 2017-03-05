package nl.lijstr.services.migrate.models;

import java.util.concurrent.ConcurrentHashMap;
import lombok.*;

/**
 * A data object that keeps track of a current migration;
 */
@Getter
public class MigrationProgress {

    private boolean finished;
    private boolean failed;
    private Exception exception;

    private final ConcurrentHashMap<String, String> updated;
    private final ConcurrentHashMap<String, String> added;
    private final ConcurrentHashMap<String, String> addedAndFilled;

    /**
     * Create a new migration progress tracker.
     */
    public MigrationProgress() {
        this.finished = false;
        this.failed = false;

        this.updated = new ConcurrentHashMap<>();
        this.added = new ConcurrentHashMap<>();
        this.addedAndFilled = new ConcurrentHashMap<>();
    }

    /**
     * The migration successfully finished.
     */
    public void finish() {
        checkState();
        this.finished = true;
    }

    /**
     * The migration failed.
     *
     * @param e The exception thrown
     */
    public void fail(Exception e) {
        finish();
        this.failed = true;
        this.exception = e;
    }

    private void checkState() {
        if (this.finished) {
            throw new IllegalStateException("This progress is already finished.");
        }
    }

    /**
     * Notify the progress that an object has been updated with a new old site ID.
     *
     * @param imdbId The IMDB ID
     * @param title  The title of the movie
     */
    public void updated(String imdbId, String title) {
        this.updated.put(imdbId, title);
    }

    /**
     * Notify the progress that a object has been added.
     *
     * @param imdbId The IMDB ID
     * @param title  The title of the movie
     */
    public void added(String imdbId, String title) {
        this.added.put(imdbId, title);
    }

    /**
     * Notify the progress that a object has been added and filled with data.
     * Requires that the IMDB ID already has been added.
     *
     * @param imdbId The IMDB ID
     */
    public void filled(String imdbId) {
        String title = this.added.remove(imdbId);
        if (title == null) {
            throw new IllegalStateException("Expected imdbId to be added already: " + imdbId);
        }
        this.addedAndFilled.put(imdbId, title);
    }


}
