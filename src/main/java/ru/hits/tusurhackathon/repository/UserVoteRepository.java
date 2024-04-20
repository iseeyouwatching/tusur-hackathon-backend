package ru.hits.tusurhackathon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hits.tusurhackathon.entity.ProposalEntity;
import ru.hits.tusurhackathon.entity.UserEntity;
import ru.hits.tusurhackathon.entity.UserVoteEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserVoteRepository extends JpaRepository<UserVoteEntity, UUID> {

    Optional<UserVoteEntity> findByUserAndProposal(UserEntity user, ProposalEntity proposal);

}
