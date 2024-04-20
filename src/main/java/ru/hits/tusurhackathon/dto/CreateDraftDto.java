package ru.hits.tusurhackathon.dto;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateDraftDto {

    private String content;
    private String jiraLink;
    private UUID projectId;


}
