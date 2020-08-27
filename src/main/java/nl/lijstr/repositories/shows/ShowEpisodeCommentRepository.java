package nl.lijstr.repositories.shows;

import nl.lijstr.domain.shows.user.ShowEpisodeComment;
import nl.lijstr.repositories.abs.BasicRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShowEpisodeCommentRepository extends BasicRepository<ShowEpisodeComment> {

    Page<ShowEpisodeComment> findByEpisodeIdOrderByCreatedDesc(Long episodeId, Pageable pageable);

}
