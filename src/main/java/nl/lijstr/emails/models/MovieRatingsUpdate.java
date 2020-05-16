package nl.lijstr.emails.models;

import lombok.Getter;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.movies.MovieRating;
import nl.lijstr.domain.users.User;
import nl.lijstr.exceptions.LijstrException;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MovieRatingsUpdate implements Comparable<MovieRatingsUpdate> {

    @Getter
    private final Movie movie;

    @Getter
    private final List<MovieRating> movieRatings;

    public MovieRatingsUpdate(Movie movie) {
        this.movie = movie;
        this.movieRatings = new ArrayList<>();
    }

    public void addRating(final MovieRating rating) {
        movieRatings.add(rating);
    }

    private Integer cachedScore = null;

    public int getScore() {
        if (cachedScore == null) {
            throw new LijstrException("Attempting to sort updates before calculatting their scores");
        }

        return cachedScore;
    }

    public void calculateScore(final User updateFor, final LocalDateTime lastUpdate) {
        int score = 0;

        // - for movies that have been added since the last update
        if (movie.getCreated().isBefore(lastUpdate)) {
            score += 1;
        }

        // + for high ranking movies (worldwide)
        if (movie.getImdbRating() != null && movie.getImdbRating() >= 7.5) {
            score += 1;
        }

        // Loop through ratings
        for (final MovieRating rating : this.movieRatings) {
            final BigDecimal ratingValue = rating.getRating();
            if (rating.getSeen() == MovieRating.Seen.YES && ratingValue != null) {
                // ++ for seen with rating
                score += 2;

                // Check if if the rating is highly different from the user's rating or the average rating
                double totalScoreForMovie = 0;
                int totalRatingsForMovie = 0;
                MovieRating forUserRating = null;
                final List<MovieRating> latestMovieRatings = movie.getLatestMovieRatings();
                for (final MovieRating latestMovieRating : latestMovieRatings) {
                    // Exclude itself from the averages
                    if (Objects.equals(latestMovieRating.getId(), rating.getId())) {
                        continue;
                    }

                    // If it has a rating, add it to the average
                    if (latestMovieRating.getRating() != null) {
                        totalScoreForMovie += latestMovieRating.getRating().doubleValue();
                        totalRatingsForMovie++;
                    }
                    if (Objects.equals(latestMovieRating.getUser().getId(), updateFor.getId())) {
                        forUserRating = latestMovieRating;
                    }
                }

                final boolean foundUserRating = forUserRating != null && forUserRating.getRating() != null;
                if (totalRatingsForMovie == 0 && !foundUserRating) {
                    // No previous ratings for this movie
                    continue;
                }


                final double baseRating = foundUserRating ? forUserRating.getRating().doubleValue() : totalScoreForMovie / totalRatingsForMovie;
                final double diff = Math.abs(baseRating - ratingValue.doubleValue());

                // Needs to be far away enough
                if (diff <= 2.5) {
                    continue;
                }

                final long pointDiff = Math.round(diff - 2);
                score += pointDiff;

            } else if (rating.getSeen() == MovieRating.Seen.YES) {
                // + for seen without rating
                score += 1;
            } else if (rating.getSeen() == MovieRating.Seen.UNKNOWN) {
                // + for not sure if seen
                score += 1;
            }

            // Interesting if there's a comment with a given length or if there's a comment while the user hasn't seen the movie
            if (!StringUtils.isEmpty(rating.getComment()) && (rating.getSeen() == MovieRating.Seen.NO || rating.getComment().length() > 10)) {
                // extra + for comment length
                final int commentLength = rating.getComment().length();
                final double lengthScore = Math.ceil(commentLength / 50.0);
                score += lengthScore > 5 ? 5 : lengthScore;
            }
        }

        this.cachedScore = score;
    }

    @Override
    public int compareTo(MovieRatingsUpdate o) {
        return Integer.compare(this.getScore(), o.getScore());
    }
}
