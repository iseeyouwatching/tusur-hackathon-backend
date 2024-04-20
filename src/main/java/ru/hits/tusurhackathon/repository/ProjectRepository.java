package ru.hits.tusurhackathon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hits.tusurhackathon.entity.ProjectEntity;
import ru.hits.tusurhackathon.entity.ProposalEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, UUID> {

    List<ProjectEntity> findAllBy();

}
