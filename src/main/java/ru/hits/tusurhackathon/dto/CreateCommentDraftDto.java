package ru.hits.tusurhackathon.dto;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateCommentDraftDto {

    private String text;
    private UUID proposalId;
    private UUID commentId;

}
