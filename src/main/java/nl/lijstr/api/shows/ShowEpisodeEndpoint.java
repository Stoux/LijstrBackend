package nl.lijstr.api.shows;

import nl.lijstr.api.abs.AbsShowService;
import nl.lijstr.api.shows.models.post.UpdateEpisodeMetaRequest;
import nl.lijstr.domain.shows.Show;
import nl.lijstr.domain.shows.ShowEpisode;
import nl.lijstr.domain.shows.ShowSeason;
import nl.lijstr.domain.shows.user.ShowEpisodeUserMeta;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.domain.users.User;
import nl.lijstr.repositories.shows.ShowEpisodeRepository;
import nl.lijstr.repositories.shows.ShowSeasonRepository;
import nl.lijstr.repositories.shows.user.ShowEpisodeUserMetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/shows/{showId:\\d+}/seasons/{seasonNumber:\\d+}/episodes", produces = MediaType.APPLICATION_JSON_VALUE)
public class ShowEpisodeEndpoint extends AbsShowService {

    @Autowired
    private ShowSeasonRepository showSeasonRepository;

    @Autowired
    private ShowEpisodeRepository showEpisodeRepository;

    @Autowired
    private ShowEpisodeUserMetaRepository showEpisodeUserMetaRepository;


    /**
     * Get the current user's {@see ShowEpisodeUserMeta} for an episode.
     *
     * @return the meta
     */
    @Secured(Permission.SHOW_USER)
    @RequestMapping("/{episodeNumber:\\d+}/meta")
    public ShowEpisodeUserMeta getUserMetaForEpisode(
        @PathVariable() final long showId,
        @PathVariable() final int seasonNumber,
        @PathVariable() final int episodeNumber
    ) {
        final ShowEpisodeUserMeta userMeta = showEpisodeUserMetaRepository.getByUserAndEpisodeShowIdAndEpisodeSeasonSeasonNumberAndEpisodeEpisodeNumber(
            getUser().toDomainUser(), showId, seasonNumber, episodeNumber
        );

        if (userMeta != null) {
            return userMeta;
        } else {
            // No UserMeta set yet, create an empty new one.
            // Episode can be empty as it wont be output in the JSON.
            return new ShowEpisodeUserMeta(null, false, null, null);
        }
    }

    /**
     * Update the current user's {@see ShowEpisodeUserMeta} for an episode.
     *
     * @return the meta
     */
    @Secured(Permission.SHOW_USER)
    @RequestMapping(value = "/{episodeNumber:\\d+}/meta", method = RequestMethod.PUT)
    public ShowEpisodeUserMeta updateUserMetaForEpisode(
        @PathVariable() final long showId,
        @PathVariable() final int seasonNumber,
        @PathVariable() final int episodeNumber,
        @Valid @RequestBody UpdateEpisodeMetaRequest request
    ) {
        final User user = getUser().toDomainUser();
        ShowEpisodeUserMeta userMeta = showEpisodeUserMetaRepository.getByUserAndEpisodeShowIdAndEpisodeSeasonSeasonNumberAndEpisodeEpisodeNumber(
            user, showId, seasonNumber, episodeNumber
        );

        if (userMeta != null) {
            userMeta.updateSeen(request.isSeen());
            userMeta.setReaction(request.getReaction());
        } else {
            // Fetch the episode
            final ShowEpisode episode = this.showEpisodeRepository.getByShowIdAndSeasonSeasonNumberAndEpisodeNumber(showId, seasonNumber, episodeNumber);
            checkIfFound(episode);

            // Create the new meta
            userMeta = new ShowEpisodeUserMeta(user, episode, request.isSeen(), request.isSeen() ? LocalDateTime.now() : null, request.getReaction());
        }

        return showEpisodeUserMetaRepository.save(userMeta);
    }

    /**
     * Check for any episodes before the given episode that the current user hasn't seen yet.
     */
    @Secured(Permission.SHOW_USER)
    @RequestMapping("/{episodeNumber:\\d+}/check-not-seen")
    public int checkForNotSeenEpisodes(
        @PathVariable() final long showId,
        @PathVariable() final int seasonNumber,
        @PathVariable() final int episodeNumber) {
        // Fetch the episode
        final ShowEpisode episode = this.showEpisodeRepository.getByShowIdAndSeasonSeasonNumberAndEpisodeNumber(showId, seasonNumber, episodeNumber);
        checkIfFound(episode);

        // Check for episodes
        final List<BigInteger> episodes = this.showEpisodeUserMetaRepository.getNotSeenEpisodesBeforeEpisode(
            getUser().getId(),
            showId,
            episode.getFollowCode()
        );

        return episodes.size();
    }

    /**
     * Update all episodes before the given episode to seen.
     */
    @Secured(Permission.SHOW_USER)
    @RequestMapping(value = "/{episodeNumber:\\d+}/update-not-seen", method = RequestMethod.PUT)
    public void updateNotSeenEpisodesToSeen(
        @PathVariable() final long showId,
        @PathVariable() final int seasonNumber,
        @PathVariable() final int episodeNumber
    ) {
        // Fetch the episode
        final ShowEpisode episode = this.showEpisodeRepository.getByShowIdAndSeasonSeasonNumberAndEpisodeNumber(showId, seasonNumber, episodeNumber);
        checkIfFound(episode);

        // Fetch the episodes
        final List<BigInteger> missingEpisodes = this.showEpisodeUserMetaRepository.getNotSeenEpisodesBeforeEpisode(
            getUser().getId(),
            showId,
            episode.getFollowCode()
        );
        final List<Long> missingEpisodeIds = missingEpisodes.stream().map(BigInteger::longValue).collect(Collectors.toList());
        final List<ShowEpisode> episodes = showEpisodeRepository.findAllByIdIn(missingEpisodeIds);

        // Fetch any userMetas the user already has
        final User user = getUser().toDomainUser();
        final List<ShowEpisodeUserMeta> userMetaList = showEpisodeUserMetaRepository.findAllByUserAndEpisodeIdIn(user, missingEpisodeIds);
        final Map<Long, ShowEpisodeUserMeta> episodeToUserMeta = userMetaList.stream().collect(Collectors.toMap(meta -> meta.getEpisode().getId(), meta -> meta));

        // Loop through episodes and create / modify meta's where needed
        final List<ShowEpisodeUserMeta> metasToUpdate = new ArrayList<>(missingEpisodes.size());
        for (final ShowEpisode showEpisode : episodes) {
            final ShowEpisodeUserMeta showEpisodeUserMeta = episodeToUserMeta.computeIfAbsent(showEpisode.getId(), episodeId -> new ShowEpisodeUserMeta(user, showEpisode, true, LocalDateTime.now(), null));
            showEpisodeUserMeta.updateSeen(true);
            metasToUpdate.add(showEpisodeUserMeta);
        }

        // Save them
        showEpisodeUserMetaRepository.save(metasToUpdate);
    }



}
