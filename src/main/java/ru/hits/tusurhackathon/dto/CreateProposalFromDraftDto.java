package ru.hits.tusurhackathon.dto;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateProposalFromDraftDto {

    private UUID draftId;

    private String content;

    private String jiraLink;

}
