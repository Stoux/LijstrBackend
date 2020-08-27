package nl.lijstr.api.shows;

import nl.lijstr.api.abs.AbsService;
import nl.lijstr.api.shows.models.post.PlaceCommentRequest;
import nl.lijstr.domain.shows.ShowEpisode;
import nl.lijstr.domain.shows.user.ShowEpisodeComment;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.repositories.shows.ShowEpisodeCommentRepository;
import nl.lijstr.repositories.shows.ShowEpisodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(value = "/shows/episodes/{episodeId:\\d+}", produces = MediaType.APPLICATION_JSON_VALUE)
public class ShowEpisodeCommentEndpoint extends AbsService {

    @Autowired
    private ShowEpisodeRepository showEpisodeRepository;

    @Autowired
    private ShowEpisodeCommentRepository showEpisodeCommentRepository;

    /**
     * Place a new comment on the episode.
     */
    @Secured(Permission.SHOW_USER)
    @Transactional
    @RequestMapping(value = "/comments", method = RequestMethod.POST)
    public ShowEpisodeComment placeComment(
        @PathVariable() final long episodeId,
        @Valid @RequestBody PlaceCommentRequest request
    ) {
        // Fetch the episode
        final ShowEpisode episode = findOne(showEpisodeRepository, episodeId, "Episode");

        // Add the comment
        // TODO: Validate the comment?
        return showEpisodeCommentRepository.save(new ShowEpisodeComment(
            getUser().toDomainUser(),
            episode,
            request.getComment(),
            request.isSpoilers()
        ));
    }

    /**
     * @param page The page to fetch (zero indexed)
     * @param perPage Number of items per page
     */
    @RequestMapping(value = "/comments", method = RequestMethod.GET)
    public Page<ShowEpisodeComment> getComments(
        @PathVariable() final long episodeId,
        @RequestParam(required = false, defaultValue = "0") @Min(1) final int page,
        @RequestParam(required = false, defaultValue = "5") @Min(1) final int perPage
    ) {
        return showEpisodeCommentRepository.findByEpisodeIdOrderByCreatedDesc(episodeId, new PageRequest(page, perPage));
    }


}
