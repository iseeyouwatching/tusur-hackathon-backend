package ru.hits.tusurhackathon.dto;

import lombok.*;
import ru.hits.tusurhackathon.entity.CommentEntity;
import ru.hits.tusurhackathon.entity.ProposalEntity;
import ru.hits.tusurhackathon.enumeration.ProposalStatus;

import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    private List<UserInfoDto> usersVotedFor;

    private List<UserInfoDto> usersVotedAgainst;

    private Boolean canVote;

    private String jiraLink;

    private Boolean canBeVoteCanceled;

    private List<CommentDto> comments;

    private long createdAt;

    public ProposalInfoDto(ProposalEntity proposal, Boolean userVote, Boolean canVote) {
        this.id = proposal.getId();
        this.text = proposal.getText();
        this.user = new UserInfoDto(proposal.getUser());
        this.proposalStatus = proposal.getProposalStatus();
        this.userVote = userVote;
        this.canVote = canVote;
        this.jiraLink = proposal.getJiraLink();
        this.comments = proposal.getComments().stream()
                .map(CommentDto::new)
                .collect(Collectors.toList());
        this.createdAt = proposal.getCreatedAt().atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

}
