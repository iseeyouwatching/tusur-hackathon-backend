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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProposalInListDto {

    private UUID id;

    private String text;

    private UserInfoDto user;

    private Boolean userVote;

    private Integer votesFor;

    private Integer votesAgainst;

    private ProposalStatus proposalStatus;

    private long createdAt;

    public ProposalInListDto(ProposalEntity proposal, Boolean userVote) {
        this.id = proposal.getId();
        this.text = proposal.getText();
        this.user = new UserInfoDto(proposal.getUser());
        this.userVote = userVote;
        this.votesFor = proposal.getVotesFor();
        this.votesAgainst = proposal.getVotesAgainst();
        this.proposalStatus = proposal.getProposalStatus();
        this.createdAt = proposal.getCreatedAt().atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

}
