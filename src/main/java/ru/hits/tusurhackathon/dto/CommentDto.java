package ru.hits.tusurhackathon.dto;

import lombok.*;
import ru.hits.tusurhackathon.entity.CommentEntity;

import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommentDto {

    private UUID id;

    private String text;

    private UserInfoDto user;

    private UUID proposalId;

    private UUID parentCommentId;

    private List<CommentDto> replies;

    private long createdAt;

    public CommentDto(CommentEntity comment) {
        this.id = comment.getId();
        this.text = comment.getText();
        this.user = new UserInfoDto(comment.getUser());
        this.proposalId = comment.getProposal().getId();
        this.parentCommentId = comment.getParentComment() != null ? comment.getParentComment().getId() : null;
        this.replies = comment.getReplies().stream()
                .map(CommentDto::new)
                .collect(Collectors.toList());
        this.createdAt = comment.getCreatedAt().atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

}
