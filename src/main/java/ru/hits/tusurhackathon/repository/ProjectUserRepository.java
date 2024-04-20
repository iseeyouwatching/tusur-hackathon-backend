package ru.hits.tusurhackathon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hits.tusurhackathon.entity.ProjectEntity;
import ru.hits.tusurhackathon.entity.ProjectUserEntity;
import ru.hits.tusurhackathon.entity.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectUserRepository extends JpaRepository<ProjectUserEntity, UUID> {

   void deleteAllByProject(ProjectEntity project);

   Optional<ProjectUserEntity> findByUserAndProject(UserEntity user, ProjectEntity project);

   List<ProjectUserEntity> findAllByUserAndIsActiveProject(UserEntity user, Boolean isActive);

   List<ProjectUserEntity> findAllByProject(ProjectEntity project);

}
