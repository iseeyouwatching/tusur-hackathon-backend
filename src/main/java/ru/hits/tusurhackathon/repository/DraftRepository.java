package ru.hits.tusurhackathon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hits.tusurhackathon.entity.CommentEntity;
import ru.hits.tusurhackathon.entity.DraftEntity;
import ru.hits.tusurhackathon.entity.ProposalEntity;
import ru.hits.tusurhackathon.entity.UserEntity;
import ru.hits.tusurhackathon.enumeration.DraftType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DraftRepository extends JpaRepository<DraftEntity, UUID> {

    List<DraftEntity> findAllByUserAndDraftType(UserEntity user, DraftType draftType);

    Optional<DraftEntity> findByUserAndProposalAndDraftType(UserEntity user, ProposalEntity proposal, DraftType draftType);

    Optional<DraftEntity> findByUserAndCommentAndDraftType(UserEntity user, CommentEntity comment, DraftType draftType);

}
