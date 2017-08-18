package nl.lijstr.api.movies;

/**
 * Created by Lorenzo on 14-7-2017.
 */

import nl.lijstr.api.abs.AbsService;
import nl.lijstr.domain.movies.Movie;
import nl.lijstr.domain.movies.MovieRating;
import nl.lijstr.domain.users.User;
import nl.lijstr.repositories.movies.MovieRepository;
import nl.lijstr.repositories.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Endpoint for recommending movies
 */
@RestController
@RequestMapping(value = "/movies/recommendations", produces = "application/json")
public class MovieRecommendationEndpoint extends AbsService {
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get the movie stats.
     *
     * @return the stats
     */

    @RequestMapping(method = RequestMethod.GET)
    public Map<String, Double> getStats() {

        int targetuser = 12; //TODO: Currently logged in user :D

        Map<String, Double> neighbours = new HashMap<>();

        List<Movie> movies = movieRepository.findAll();

        List<Movie> myWatchedMovies = new ArrayList<>();

        //Get all rated movies by currently logged in user
        for (Movie movie : movies) {
            for (MovieRating rating : movie.getMovieRatings()) {
                if (rating.getUser().getId() == targetuser && rating.getSeen() == MovieRating.Seen.YES && rating.getRating() != null) {
                    myWatchedMovies.add(movie);
                    break;
                }
            }
        }

        List<User> users = userRepository.findAll();

        for (User user : users) {
            if (user.getId() == targetuser || user.getId() == 1) continue;

            Double sumXY = 0.0;     // sum of x * y
            Double sumXX = 0.0;     // sum of x^2
            Double sumYY = 0.0;     // sum of y^2

            for (Movie watchedMovie : myWatchedMovies) {
                MovieRating targetRating = null;
                MovieRating userRating = null;

                for (MovieRating watchedRating : watchedMovie.getLatestMovieRatings()) {
                    if (watchedRating.getUser().getId() == targetuser) {
                        targetRating = watchedRating;
                    }
                    if (Objects.equals(watchedRating.getUser().getId(), user.getId())) {
                        userRating = watchedRating;
                    }
                }

                // rating from other user
                Double ratingX = userRating == null || userRating.getRating() == null ? Double.valueOf(0.0) : userRating.getRating();

                // rating from target user
                Double ratingY = targetRating.getRating() == null ? Double.valueOf(0.0) : targetRating.getRating();

                sumXY += (ratingX * ratingY);
                sumXX += (ratingX * ratingX);
                sumYY += (ratingY * ratingY);
            }

            sumXX = Math.sqrt(sumXX);
            sumYY = Math.sqrt(sumYY);

            double result = sumXX == 0 || sumYY == 0 ? 0 : sumXY / (sumXX * sumYY);

            if (result > -1) {
                if (neighbours.size() < 10) {
                    neighbours.put(user.getDisplayName(), result);
                } else {
                    double currentMinimum = Collections.min(neighbours.values());
                    if (result > currentMinimum) {
                        Iterator it = neighbours.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry) it.next();
                            if ((double) pair.getValue() == currentMinimum) {
                                System.out.println("Removing: " + pair.getKey() + " - " + pair.getValue());
                                it.remove(); // avoids a ConcurrentModificationException
                            }
                        }

                        System.out.println("Adding: " + user.getDisplayName() + " - " + result);
                        neighbours.put(user.getDisplayName(), result);
                    }
                }
            }
        }


        return neighbours;
    }

}
