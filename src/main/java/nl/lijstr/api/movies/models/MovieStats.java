package nl.lijstr.api.movies.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.*;
import nl.lijstr.domain.users.User;

@Getter
@Setter
public class MovieStats {

    private int numberOfMovies;
    private double averageImdb;
    private double averageMetacritic;
    private Map<Integer, Integer> moviesPerYear;
    private Map<Long, UserStats> userToStats;

    public MovieStats(List<User> forUsers) {
        this.moviesPerYear = new TreeMap<>();
        this.userToStats = new HashMap<>();

        for (User user : forUsers) {
            this.userToStats.put(user.getId(), new UserStats());
        }
    }

    public void incrementYear(Integer year) {
        int movies = 1;
        if (moviesPerYear.containsKey(year)) {
            movies += moviesPerYear.get(year);
        }
        moviesPerYear.put(year, movies);
    }

    public UserStats getUser(long id) {
        return userToStats.get(id);
    }

    public class UserStats {

        public int added;
        public int filledIn;

        public int seen;
        public int notSeen;
        public int noIdea;
        public int unknownRating;
        public int withComment;

        @JsonIgnore
        private int nrOfRatings;
        @JsonIgnore
        private double totalRating;

        public void addRating(double rating) {
            nrOfRatings++;
            totalRating += rating;
        }

        @JsonProperty
        public double getAverageRating() {
            if (nrOfRatings != 0 && totalRating != 0) {
                return totalRating / nrOfRatings;
            } else {
                return 0;
            }
        }

    }

}