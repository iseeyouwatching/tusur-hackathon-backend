package ru.hits.tusurhackathon.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EditProposalDto {

    private String text;

    private String jiraLink;

}
