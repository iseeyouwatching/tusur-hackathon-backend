package ru.hits.tusurhackathon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.tusurhackathon.dto.*;
import ru.hits.tusurhackathon.service.ProjectService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Проекты.")
public class ProjectController {

    private final ProjectService projectService;

    @Operation(
            summary = "Получить список предложений проекта.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{projectId}/proposals")
    public ResponseEntity<List<ProposalInListDto>> getProjectProposals(@PathVariable UUID projectId) {
        return new ResponseEntity<>(projectService.getProjectProposals(projectId), HttpStatus.OK);
    }

    @Operation(
            summary = "Получить список проектов пользователя.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<List<ProjectInListDto>> getUserProjects() {
        return new ResponseEntity<>(projectService.getUserProjects(), HttpStatus.OK);
    }

    @Operation(
            summary = "Создать проект.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    public ResponseEntity<ProjectInfoDto> createProject(@RequestBody @Valid CreateProjectDto createProjectDto) {
        return new ResponseEntity<>(projectService.createProject(createProjectDto), HttpStatus.OK);
    }

    @Operation(
            summary = "Получить информацию о проекте.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    public ResponseEntity<ProjectInfoDto> getProjectInfo(@PathVariable("id") UUID projectId) {
        return new ResponseEntity<>(projectService.getProjectInfo(projectId), HttpStatus.OK);
    }

    @Operation(
            summary = "Удалить проект.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable("id") UUID projectId) {
        projectService.deleteProject(projectId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Сделать проект активным.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/{id}/activate")
    public ResponseEntity<Void> activateProject(@PathVariable("id") UUID projectId) {
        projectService.activateProject(projectId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Получить идентификатор активного проекта.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/active")
    public ResponseEntity<ActiveProjectDto> getActiveProject() {
        return new ResponseEntity<>(projectService.getActiveProject(), HttpStatus.OK);
    }

    @Operation(
            summary = "Редактировать проект.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}")
    public ResponseEntity<ProjectInfoDto> editProject(@PathVariable("id") UUID projectId,
                                                      @RequestBody @Valid EditProjectDto editProjectDto) {
        return new ResponseEntity<>(projectService.editProject(projectId, editProjectDto), HttpStatus.OK);
    }

    @Operation(
            summary = "Добавить участников в проект.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/{id}/add-participants")
    public ResponseEntity<ProjectInfoDto> addParticipantsToProject(@PathVariable("id") UUID projectId,
                                                         @RequestBody @Valid AddParticipantsToProjectDto addParticipantsToProjectDto) {
        return new ResponseEntity<>(projectService.addParticipantsToProject(projectId, addParticipantsToProjectDto), HttpStatus.OK);
    }

    @Operation(
            summary = "Удалить участников из проекта.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}/users")
    public ResponseEntity<Void> removeUsersFromProject(@PathVariable("id") UUID projectId, @RequestBody List<UUID> userIds) {
        projectService.removeUsersFromProject(projectId, userIds);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Назначить роль админа проекта.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{projectId}/assign-admin-role/{userId}")
    public ResponseEntity<Void> assignAdminRoleToUser(@PathVariable UUID projectId, @PathVariable UUID userId) {
        projectService.assignAdminRoleToUser(projectId, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Снять роль админа проекта.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{projectId}/remove-admin-role/{userId}")
    public ResponseEntity<Void> removeAdminRoleFromUser(@PathVariable UUID projectId, @PathVariable UUID userId) {
        projectService.removeAdminRoleFromUser(projectId, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
