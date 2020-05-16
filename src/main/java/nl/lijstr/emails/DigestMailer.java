package nl.lijstr.emails;

import nl.lijstr.api.abs.AbsService;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.movies.MovieRating;
import nl.lijstr.domain.users.User;
import nl.lijstr.emails.models.MovieRatingsUpdate;
import nl.lijstr.repositories.movies.MovieRatingRepository;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.services.kanye.KanyeApiService;
import nl.lijstr.services.mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Component that has the ability to send users their updates (digest) email.
 */
@Component
public class DigestMailer extends AbsService  {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieRatingRepository movieRatingRepository;

    @Autowired
    private KanyeApiService kanyeApiService;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MailService mailService;

    @Value("${host.app}")
    private String appUrl;
    @Value("${host.api}")
    private String apiUrl;

    @Value("${emails.movies.digest}")
    private boolean sendMail;

    @Autowired
    private Random random;

    /**
     * Send an update / digest mail to the given user.
     *
     * @param user The user
     * @param since Fetch all updates since the give time
     */
    public void sendDigestTo(final User user, final LocalDateTime since) {
        final Context context = buildContext(user, since);
        final String htmlContent = templateEngine.process("templates/emails/digest.html", context);
        if (this.sendMail) {
            this.mailService.sendMail(
                "Lijstr.nl updates",
                user,
                htmlContent,
                String.format("Hi %s. Een Lijstr updateje voor joe.", user.getDisplayName()),
                "digest"
            );
        }
    }

    private Context buildContext(final User user, final LocalDateTime since) {
        final Context context = new Context();

        // User & App info
        context.setVariable("appUrl", this.appUrl);
        context.setVariable("apiUrl", this.apiUrl);
        context.setVariable("user", user);
        context.setVariable("lastUpdate", since);

        // Meem texts
        context.setVariable("title", pickRandom(
            "Een Lijstr updateje voor joe.",
            "Malse updates, vers in de mailbox.",
            "Film nieuws, maar dan ghetto.",
            "Iemand nog wat leuks gekeken? Dacht 't niet nee."
        ));

        context.setVariable("algoPrefix", pickRandom(
            "Hoge kwaliteit",
            "Hyperdeluxe",
            "Ultra moderne",
            "Ultra geavanceerde"
        ));
        context.setVariable("algoBuzzwords", String.join(" ", pickRandomMultiple(
            3,
            "z-index",
            "AI",
            "kunstmatige",
            "5g coronavirus-enhanced",
            "neuraal netwerk",
            "interlinked",
            "hyperdrive"
        )));


        // Fetch all movies since the given time
        final List<Movie> addedMovies = movieRepository.findAllByCreatedAfter(since);
        Collections.shuffle(addedMovies, random);
        context.setVariable(
            "addedMoviesSubset",
            // Only show 3 or less
            addedMovies.size() > 3 ? addedMovies.subList(0, 3) : addedMovies
        );
        context.setVariable("addedMoviesCount", addedMovies.size());

        // Outstanding
        context.setVariable("outstandingCount", getOutstandingMoviesCount(user.getId()));

        // Ratings
        final List<MovieRatingsUpdate> updates = determineMovieRatings(user, since);
        context.setVariable(
            "ratingUpdates",
            updates.size() > 3 ? updates.subList(0, 3) : updates
        );

        final Function<MovieRating, String> formatRating = movieRating -> {
            final BigDecimal rating = movieRating.getRating();
            return rating == null ? "?" : String.format("%.1f", rating.doubleValue());
        };
        context.setVariable(
            "formatRating",
            formatRating
        );

        final long otherRatings;
        if (updates.size() <= 3) {
            otherRatings = 0;
        } else {
            otherRatings = updates.subList(3, updates.size()).stream()
                .map(MovieRatingsUpdate::getMovieRatings)
                .flatMap(Collection::stream)
                .filter(rating -> rating.getSeen() != MovieRating.Seen.NO || rating.hasComment())
                .count();
        }
        context.setVariable("otherRatingUpdates", otherRatings);

        // Kanye
        context.setVariable("kanyeQuote", kanyeApiService.getQuote());

        return context;
    }

    /**
     * Find all interesting updates per movie, ordered by 'interesting'-score.
     *
     * @param user Who to fetch the updates for
     * @param since Since when to fetch updates
     *
     * @return list of updates
     */
    private List<MovieRatingsUpdate> determineMovieRatings(final User user, final LocalDateTime since) {
        final List<MovieRating> foundRatings = movieRatingRepository.findAllByUserNotAndLastModifiedAfterAndLatestIsTrue(user, since);
        final Map<Long, MovieRatingsUpdate> idToMovieUpdate = new HashMap<>();

        for (final MovieRating foundRating : foundRatings) {
            final Movie movie = foundRating.getMovie();
            final MovieRatingsUpdate update = idToMovieUpdate.computeIfAbsent(movie.getId(), movieId -> new MovieRatingsUpdate(movie));
            update.addRating(foundRating);
        }

        final List<MovieRatingsUpdate> updates = idToMovieUpdate.values().stream()
            .peek(update -> update.calculateScore(user, since))
            .sorted()
            .collect(Collectors.toList());

        Collections.reverse(updates);

        return updates;
    }


    @SafeVarargs
    private final <X> X pickRandom(X... items) {
        return items[random.nextInt(items.length)];
    }

    @SafeVarargs
    private final <X> List<X> pickRandomMultiple(int numberOfItems, X... items) {
        final List<X> list = Arrays.asList(items);
        Collections.shuffle(list);
        return list.subList(0, numberOfItems);
    }

    private long getOutstandingMoviesCount(long userId) {
        return movieRepository.findAllByOrderByTitleAsc().stream()
            .filter(m -> !hasRating(userId, m))
            .count();
    }

    private boolean hasRating(long userId, Movie movie) {
        return movie.getLatestMovieRatings().stream()
            .anyMatch(movieRating -> movieRating.getUser().getId().equals(userId));
    }


}
