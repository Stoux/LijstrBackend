package nl.lijstr.repositories.shows.user;

import nl.lijstr.domain.shows.Show;
import nl.lijstr.domain.shows.ShowEpisode;
import nl.lijstr.domain.shows.user.ShowEpisodeUserMeta;
import nl.lijstr.domain.users.User;
import nl.lijstr.repositories.abs.BasicRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

public interface ShowEpisodeUserMetaRepository extends BasicRepository<ShowEpisodeUserMeta> {

    /**
     * Fetch a user's meta (if any).
     * @param episode The episode
     * @param user The user
     * @return the meta (if found)
     */
    ShowEpisodeUserMeta getByEpisodeAndUser(ShowEpisode episode, User user);

    /**
     *
     * @param user
     * @param showId
     * @param seasonNumber
     * @param episodeNumber
     * @return
     */
    ShowEpisodeUserMeta getByUserAndEpisodeShowIdAndEpisodeSeasonSeasonNumberAndEpisodeEpisodeNumber(User user, Long showId, Integer seasonNumber, Integer episodeNumber);

    /**
     * Find all meta values for the given user + episodes
     */
    List<ShowEpisodeUserMeta> findAllByUserAndEpisodeIdIn(User user, Collection<Long> episodeIds);

    List<ShowEpisodeUserMeta> findAllByUserAndEpisodeShow(User user, Show show);


    /**
     * Fetch all episodes that user hasn't seen, before the given episode.
     *
     * @param userId ID of the user
     * @param showId ID of the show
     * @param episodeFollowCode Code of the last episode that you want to check to (excluding). code = ((seasonNumber * 10000) + episodeNumber)
     *
     * @return list of episode IDs
     */
    @Query(
        nativeQuery = true,
        value = "SELECT se.id as id FROM show_episode se " +
            "JOIN show_season ss on se.season_id = ss.id AND se.show_id = :show " +
            "LEFT JOIN show_episode_user_meta seum on se.id = seum.episode_id AND seum.user_id = :user " +
            "WHERE ss.season_number != 0 AND (seum.user_id IS NULL OR seum.seen = false) AND ((ss.season_number * 10000) + se.episode_number) < :number"
    )
    List<BigInteger> getNotSeenEpisodesBeforeEpisode(@Param("user") Long userId, @Param("show") Long showId, @Param("number") Integer episodeFollowCode);



}
