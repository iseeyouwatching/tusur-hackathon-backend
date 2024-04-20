package ru.hits.tusurhackathon.dto;

import lombok.*;
import ru.hits.tusurhackathon.entity.CommentEntity;
import ru.hits.tusurhackathon.entity.ProposalEntity;
import ru.hits.tusurhackathon.enumeration.ProposalStatus;

import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProposalInfoDto {

    private UUID id;

    private String text;

    private UserInfoDto user;

    private ProposalStatus proposalStatus;

    private Boolean userVote;

    private Boolean canVote;

    private String jiraLink;

    private Boolean canBeVoteCanceled;

    private List<CommentEntity> comments;

    private long createdAt;

    public ProposalInfoDto(ProposalEntity proposal, Boolean userVote, Boolean canVote) {
        this.id = proposal.getId();
        this.text = proposal.getText();
        this.user = new UserInfoDto(proposal.getUser());
        this.proposalStatus = proposal.getProposalStatus();
        this.userVote = userVote;
        this.canVote = canVote;
        this.jiraLink = proposal.getJiraLink();
        this.comments = proposal.getComments();
        this.createdAt = proposal.getCreatedAt().atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

}
