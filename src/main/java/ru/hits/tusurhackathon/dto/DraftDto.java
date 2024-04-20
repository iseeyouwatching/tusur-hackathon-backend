package ru.hits.tusurhackathon.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
public class DraftDto {

    private UUID id;

    private String content;

    private String jiraLink;

    private LocalDateTime savedAt;

}
