package ru.hits.tusurhackathon.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.hits.tusurhackathon.entity.ProposalEntity;
import ru.hits.tusurhackathon.enumeration.ProposalStatus;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProposalInListDto {

    private UUID id;

    private String text;

    private String username;

    private Integer votesFor;

    private ProposalStatus proposalStatus;

    private long createdAt;

    public ProposalInListDto(ProposalEntity proposal) {
        this.id = proposal.getId();
        this.text = proposal.getText();
        this.username = proposal.getUser().getUsername();
        this.votesFor = proposal.getVotesFor();
        this.proposalStatus = proposal.getProposalStatus();
        this.createdAt = proposal.getCreatedAt().atZone(ZoneOffset.UTC).toInstant().toEpochMilli(); // Преобразование в миллисекунды epoch
    }

}
