package ru.hits.tusurhackathon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.tusurhackathon.dto.*;
import ru.hits.tusurhackathon.entity.ProjectEntity;
import ru.hits.tusurhackathon.entity.ProjectUserEntity;
import ru.hits.tusurhackathon.entity.UserEntity;
import ru.hits.tusurhackathon.exception.ForbiddenException;
import ru.hits.tusurhackathon.exception.NotFoundException;
import ru.hits.tusurhackathon.repository.ProjectRepository;
import ru.hits.tusurhackathon.repository.ProjectUserRepository;
import ru.hits.tusurhackathon.repository.UserRepository;
import ru.hits.tusurhackathon.security.JwtUserData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectUserRepository projectUserRepository;

    public List<ProjectInListDto> getUserProjects() {
        UUID authenticatedUserId = getAuthenticatedUserId();

        UserEntity user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new NotFoundException("Пользователя с ID " + authenticatedUserId + " не существует"));

        List<ProjectEntity> userProjects = user.getProjects();

        return userProjects.stream()
                .map(ProjectInListDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProjectInfoDto createProject(CreateProjectDto createProjectDto) {
        UUID authenticatedUserId = getAuthenticatedUserId();

        UserEntity user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new NotFoundException("Пользователя с ID " + authenticatedUserId + " не существует"));

        ProjectEntity project = ProjectEntity.builder()
                .name(createProjectDto.getName())
                .schedule(createProjectDto.getSchedule())
                .votesPerPeriod(createProjectDto.getVotesPerPeriod())
                .votesRefreshPeriodDays(createProjectDto.getVotesRefreshPeriodDays())
                .createdAt(LocalDateTime.now())
                .users(List.of(user))
                .build();

        project = projectRepository.save(project);

        ProjectUserEntity projectUser = ProjectUserEntity.builder()
                .project(project)
                .user(user)
                .numberOfVotes(0)
                .isAdmin(true)
                .isActiveProject(true)
                .build();

        projectUserRepository.save(projectUser);

        return new ProjectInfoDto(project, List.of(projectUser));
    }

    public ProjectInfoDto getProjectInfo(UUID projectId) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Проект с ID " + projectId + " не найден"));

        List<ProjectUserEntity> projectUsers = projectUserRepository.findAllByProject(project);

        return new ProjectInfoDto(project, projectUsers);
    }

    @Transactional
    public void deleteProject(UUID projectId) {
        UUID authenticatedUserId = getAuthenticatedUserId();

        UserEntity user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new NotFoundException("Пользователя с ID " + authenticatedUserId + " не существует"));

        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Проект с ID " + projectId + " не найден"));

        // Проверяем, имеет ли пользователь доступ к проекту и админские права
        if (!hasAdminRights(project, user)) {
            throw new ForbiddenException("У вас нет прав для удаления этого проекта");
        }

        projectUserRepository.deleteAllByProject(project);

        // Удаляем проект и связанные с ним записи ProjectUserEntity
        projectRepository.delete(project);
    }

    @Transactional
    public void activateProject(UUID projectId) {
        UUID authenticatedUserId = getAuthenticatedUserId();

        UserEntity user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new NotFoundException("Пользователя с ID " + authenticatedUserId + " не существует"));

        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Проект с ID " + projectId + " не найден"));

        ProjectUserEntity projectUser = projectUserRepository.findByUserAndProject(user, project).get();

        projectUser.setIsActiveProject(true);
        projectUserRepository.save(projectUser);

        deactivatePreviousActiveProject(user, projectId);
    }

    public ActiveProjectDto getActiveProject() {
        UUID authenticatedUserId = getAuthenticatedUserId();

        UserEntity user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new NotFoundException("Пользователя с ID " + authenticatedUserId + " не существует"));

        ProjectUserEntity projectUser = projectUserRepository.findAllByUserAndIsActiveProject(user, true).get(0);

        return new ActiveProjectDto(projectUser.getProject().getId());
    }

    @Transactional
    public ProjectInfoDto editProject(UUID projectId, EditProjectDto editProjectDto) {
        UUID authenticatedUserId = getAuthenticatedUserId();

        UserEntity user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new NotFoundException("Пользователя с ID " + authenticatedUserId + " не существует"));

        // Получение проекта по его ID
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Проект с ID " + projectId + " не найден"));

        // Проверяем, имеет ли пользователь доступ к проекту и админские права
        if (!hasAdminRights(project, user)) {
            throw new ForbiddenException("У вас нет прав для редактирования этого проекта");
        }

        if (editProjectDto.getName() != null) {
            project.setName(editProjectDto.getName());
        }

        if (editProjectDto.getSchedule() != null) {
            project.setSchedule(editProjectDto.getSchedule());
        }

        if (editProjectDto.getVotesPerPeriod() != null) {
            project.setVotesPerPeriod(editProjectDto.getVotesPerPeriod());
        }

        if (editProjectDto.getVotesRefreshPeriodDays() != null) {
            project.setVotesRefreshPeriodDays(editProjectDto.getVotesRefreshPeriodDays());
        }

        // Сохранение обновленного проекта в базе данных
        project = projectRepository.save(project);

        List<ProjectUserEntity> projectUsers = projectUserRepository.findAllByProject(project);

        return new ProjectInfoDto(project, projectUsers);
    }

    @Transactional
    public ProjectInfoDto addParticipantsToProject(UUID projectId, AddParticipantsToProjectDto addParticipantsToProjectDto) {
        // Получение аутентифицированного пользователя
        UUID authenticatedUserId = getAuthenticatedUserId();
        UserEntity authenticatedUser = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + authenticatedUserId + " не найден"));

        // Получение проекта по его ID
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Проект с ID " + projectId + " не найден"));

        // Проверка, имеет ли пользователь доступ к проекту и админские права
        if (!hasAdminRights(project, authenticatedUser)) {
            throw new ForbiddenException("У вас нет прав для того, чтобы добавлять участников в этот проект");
        }

        // Получение пользователей по их ID
        List<UserEntity> participants = userRepository.findAllById(addParticipantsToProjectDto.getUserIds());

        // Проверка, что все указанные пользователи существуют
        if (participants.size() != addParticipantsToProjectDto.getUserIds().size()) {
            throw new NotFoundException("Один или несколько пользователей не найдены");
        }

        // Добавление пользователей к проекту
        for (UserEntity participant : participants) {
            ProjectUserEntity projectUser = ProjectUserEntity.builder()
                    .project(project)
                    .user(participant)
                    .numberOfVotes(0)
                    .isAdmin(false) // По умолчанию новые участники не являются администраторами проекта
                    .build();
            projectUserRepository.save(projectUser);

            project.getUsers().add(participant);
        }

        projectRepository.save(project);

        List<ProjectUserEntity> projectUsers = projectUserRepository.findAllByProject(project);

        return new ProjectInfoDto(project, projectUsers);
    }

    @Transactional
    public void removeUsersFromProject(UUID projectId, List<UUID> userIds) {
        // Проверяем, существует ли проект с заданным ID
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Проект с ID " + projectId + " не найден"));

        // Проверяем, имеет ли пользователь доступ к проекту и админские права
        UUID authenticatedUserId = getAuthenticatedUserId();
        UserEntity user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + authenticatedUserId + " не существует"));

        if (!hasAdminRights(project, user)) {
            throw new ForbiddenException("У вас нет прав для удаления участников из этого проекта");
        }

        // Удаляем указанных пользователей из проекта
        for (UUID userId : userIds) {
            UserEntity userToRemove = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не существует"));

            ProjectUserEntity projectUser = projectUserRepository.findByUserAndProject(userToRemove, project)
                    .orElseThrow(() -> new NotFoundException("Указанный пользователь не является участником проекта"));

            projectUserRepository.delete(projectUser);
            project.getUsers().remove(userToRemove);
        }

        projectRepository.save(project);
    }

    @Transactional
    public void assignAdminRoleToUser(UUID projectId, UUID userId) {
        // Проверяем существование проекта
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Проект с ID " + projectId + " не найден"));

        // Проверяем, имеет ли пользователь доступ к проекту и админские права
        UUID authenticatedUserId = getAuthenticatedUserId();
        UserEntity authenticatedUser = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + authenticatedUserId + " не существует"));

        if (!hasAdminRights(project, authenticatedUser)) {
            throw new ForbiddenException("У вас нет прав для управления ролями");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не существует"));

        // Находим соответствующую запись о пользователе в проекте
        ProjectUserEntity projectUser = projectUserRepository.findByUserAndProject(user, project)
                .orElseThrow(() -> new NotFoundException("Запись о пользователе с ID " + userId + " в проекте с ID " + projectId + " не найдена"));

        // Назначаем пользователю роль администратора в проекте
        projectUser.setIsAdmin(true);

        // Сохраняем обновленную запись в базе данных
        projectUserRepository.save(projectUser);
    }

    @Transactional
    public void removeAdminRoleFromUser(UUID projectId, UUID userId) {
        // Проверяем существование проекта
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Проект с ID " + projectId + " не найден"));

        // Проверяем, имеет ли пользователь доступ к проекту и админские права
        UUID authenticatedUserId = getAuthenticatedUserId();
        UserEntity authenticatedUser = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + authenticatedUserId + " не существует"));

        if (!hasAdminRights(project, authenticatedUser)) {
            throw new ForbiddenException("У вас нет прав для управления ролями");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не существует"));

        // Находим соответствующую запись о пользователе в проекте
        ProjectUserEntity projectUser = projectUserRepository.findByUserAndProject(user, project)
                .orElseThrow(() -> new NotFoundException("Запись о пользователе с ID " + userId + " в проекте с ID " + projectId + " не найдена"));

        // Назначаем пользователю роль администратора в проекте
        projectUser.setIsAdmin(false);

        // Сохраняем обновленную запись в базе данных
        projectUserRepository.save(projectUser);
    }

    private void deactivatePreviousActiveProject(UserEntity user, UUID projectId) {
        List<ProjectUserEntity> userProjects = projectUserRepository.findAllByUserAndIsActiveProject(user, true);
        for (ProjectUserEntity projectUser : userProjects) {
            if (!projectUser.getProject().getId().equals(projectId)) {
                projectUser.setIsActiveProject(false);
                projectUserRepository.save(projectUser);
            }
        }
    }

    private boolean hasAdminRights(ProjectEntity project, UserEntity user) {
        Optional<ProjectUserEntity> projectUser = projectUserRepository.findByUserAndProject(user, project);

        return projectUser.isPresent() && projectUser.get().getIsAdmin();
    }

    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        return userData.getId();
    }

}
