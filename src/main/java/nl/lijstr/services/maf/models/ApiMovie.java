package nl.lijstr.services.maf.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.*;

/**
 * Created by Stoux on 03/12/2015.
 */
@Getter
public class ApiMovie {

    @SerializedName("idIMDB")
    private String imdbId;

    //General information
    private String title;
    private String originalTitle;
    private String year;
    private String releaseDate;

    //Plot
    @SerializedName("simplePlot")
    private String shortPlot;
    @SerializedName("plot")
    private String longPlot;

    //Scores
    private String rating;
    private String metascore;
    @SerializedName("votes")
    private String nrOfVotes;

    //This movie is rated X
    @SerializedName("rated")
    private String ageRating;

    //Runtimes
    private String runtime;
    private Technical technical;

    @SerializedName("urlPoster")
    private String posterUrl;

    //Random stuff
    private List<String> languages;
    @SerializedName("movieTrivia")
    private List<String> trivia;

    private String type;

    //External
    private List<ApiPerson> directors;
    private List<ApiPerson> writers;
    private List<ApiActor> actors;
    private List<String> genres;

    /**
     * Technical subclass.
     */
    @ToString
    @Getter
    public static class Technical {
        @SerializedName("runtime")
        private List<String> runtimes;
    }

}
