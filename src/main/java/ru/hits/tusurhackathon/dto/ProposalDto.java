package ru.hits.tusurhackathon.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.hits.tusurhackathon.entity.CommentEntity;
import ru.hits.tusurhackathon.entity.ProposalEntity;
import ru.hits.tusurhackathon.enumeration.ProposalStatus;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProposalDto {

    private UUID id;

    private String text;

    private UserInfoDto user;

    private Integer votesFor;

    private Integer votesAgainst;

    private ProposalStatus proposalStatus;

    private List<CommentDto> comments;

    private long createdAt;

    public ProposalDto(ProposalEntity proposal) {
        this.id = proposal.getId();
        this.text = proposal.getText();
        this.user = new UserInfoDto(proposal.getUser());
        this.votesFor = proposal.getVotesFor();
        this.votesAgainst = proposal.getVotesAgainst();
        this.proposalStatus = proposal.getProposalStatus();
        this.comments = proposal.getComments().stream()
                .map(CommentDto::new)
                .collect(Collectors.toList());
        this.createdAt = proposal.getCreatedAt().atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

}
