package nl.lijstr.domain.movies;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.lijstr.domain.base.IdCmModel;
import nl.lijstr.services.modify.annotations.ModifiableWithHistory;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Model that indicates a collection of movies that are related to eachother for some reason.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ModifiableWithHistory
public class MovieCollection extends IdCmModel {

    private String title;
    @Column(length = 1000)
    private String keywords;
    private String description;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.PERSIST)
    private List<Movie> movies;

    @JsonProperty("movieIds")
    public List<Long> getMovieIds() {
        return movies.stream().map(Movie::getId).collect(Collectors.toList());
    }

}
