package nl.lijstr.api.shows;

import nl.lijstr.api.abs.AbsShowService;
import nl.lijstr.domain.shows.Show;
import nl.lijstr.domain.shows.ShowSeason;
import nl.lijstr.repositories.shows.ShowSeasonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/shows/{showId:\\d+}/seasons", produces = MediaType.APPLICATION_JSON_VALUE)
public class ShowSeasonEndpoint extends AbsShowService {

    @Autowired
    private ShowSeasonRepository showSeasonRepository;

    /**
     * Get a {@link Show} as detail view.
     * @return the show season
     */
    @RequestMapping("/{seasonNumber:\\d+}")
    public ShowSeason getById(@PathVariable("showId") final long showId, @PathVariable("seasonNumber") final int seasonNumber) {
        final ShowSeason season = showSeasonRepository.getByShowIdAndSeasonNumber(showId, seasonNumber);
        checkIfFound(season);
        return season;
    }


}
