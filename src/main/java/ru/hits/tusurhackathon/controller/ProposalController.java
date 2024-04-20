package ru.hits.tusurhackathon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.tusurhackathon.dto.CreateProposalDto;
import ru.hits.tusurhackathon.dto.ProposalDto;
import ru.hits.tusurhackathon.dto.ProposalInListDto;
import ru.hits.tusurhackathon.dto.ProposalVoteDto;
import ru.hits.tusurhackathon.service.ProposalService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/proposals")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Предложения.")
public class ProposalController {

    private final ProposalService proposalService;

    @Operation(
            summary = "Получить список предложений.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<List<ProposalInListDto>> getProposals() {
        return new ResponseEntity<>(proposalService.getProposals(), HttpStatus.OK);
    }

    @Operation(
            summary = "Создать предложение.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    public ResponseEntity<ProposalDto> createProposal(@RequestBody @Valid CreateProposalDto createProposalDto) {
        return new ResponseEntity<>(proposalService.createProposal(createProposalDto), HttpStatus.OK);
    }

    @Operation(
            summary = "Проголосовать за/против предложение(-я).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/{proposalId}/vote")
    public ResponseEntity<Void> vote(@PathVariable UUID proposalId, @RequestBody @Valid ProposalVoteDto proposalVoteDto) {
        proposalService.voteForOrAgainstProposal(proposalId, proposalVoteDto.getIsUpvote());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Отменить голос.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/{proposalId}/cancel-vote")
    public ResponseEntity<Void> cancelVote(@PathVariable UUID proposalId) {
        proposalService.cancelVote(proposalId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
