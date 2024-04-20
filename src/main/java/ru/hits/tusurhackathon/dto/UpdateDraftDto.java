package ru.hits.tusurhackathon.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateDraftDto {

    private String content;
    private String jiraLink;

}
