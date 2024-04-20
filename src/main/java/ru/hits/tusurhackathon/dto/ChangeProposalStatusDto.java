package ru.hits.tusurhackathon.dto;

import lombok.*;
import ru.hits.tusurhackathon.enumeration.ProposalStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChangeProposalStatusDto {

    private ProposalStatus proposalStatus;

}
