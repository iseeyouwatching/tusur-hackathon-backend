package ru.hits.tusurhackathon.dto;

import lombok.*;
import ru.hits.tusurhackathon.entity.ProjectEntity;
import ru.hits.tusurhackathon.entity.ProjectUserEntity;

import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProjectInfoDto {

    private UUID id;

    private String name;

    private String schedule;

    private Integer votesPerPeriod;

    private Integer votesRefreshPeriodDays;

    private long createdAt;

    private List<ProjectUserInfoDto> users;

    public ProjectInfoDto(ProjectEntity project, List<ProjectUserEntity> projectUsers) {
        this.id = project.getId();
        this.name = project.getName();
        this.schedule = project.getSchedule();
        this.votesPerPeriod = project.getVotesPerPeriod();
        this.votesRefreshPeriodDays = project.getVotesRefreshPeriodDays();
        this.createdAt = project.getCreatedAt().atZone(ZoneOffset.UTC).toInstant().toEpochMilli(); // Преобразование в миллисекунды epoch
        this.users = projectUsers.stream()
                .map(projectUser -> new ProjectUserInfoDto(projectUser.getUser(), project.getVotesPerPeriod() - projectUser.getNumberOfVotes(), projectUser.getIsAdmin()))
                .collect(Collectors.toList());
    }

}
