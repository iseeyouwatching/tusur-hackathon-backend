package ru.hits.tusurhackathon.dto;

import lombok.*;
import ru.hits.tusurhackathon.entity.ProjectEntity;

import java.time.ZoneOffset;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProjectInListDto {

    private UUID id;

    private String name;

    private int participantCount;

    private long createdAt;

    public ProjectInListDto(ProjectEntity project) {
        this.id = project.getId();
        this.name = project.getName();
        this.participantCount = project.getUsers().size();
        this.createdAt = project.getCreatedAt().atZone(ZoneOffset.UTC).toInstant().toEpochMilli(); // Преобразование в миллисекунды epoch
    }

}
