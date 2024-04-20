package ru.hits.tusurhackathon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hits.tusurhackathon.entity.CommentEntity;
import ru.hits.tusurhackathon.entity.DraftEntity;

import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, UUID> {

}
