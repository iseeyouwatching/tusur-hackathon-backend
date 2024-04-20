package ru.hits.tusurhackathon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.tusurhackathon.dto.*;
import ru.hits.tusurhackathon.service.DraftService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/drafts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Черновики.")
public class DraftController {

    private final DraftService draftService;

    @Operation(
            summary = "Создать черновик-предложение.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/proposal")
    public ResponseEntity<Void> createProposalDraft(@RequestBody @Valid CreateDraftDto createDraftDto) {
        draftService.createProposalDraft(createDraftDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Создать черновик-комментарий.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/comment")
    public ResponseEntity<Void> createCommentDraft(@RequestBody @Valid CreateCommentDraftDto createCommentDraftDto) {
        draftService.createCommentDraft(createCommentDraftDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Создать предложение из черновика.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/proposal-from-draft")
    public ResponseEntity<Void> createProposalFromDraft(@RequestBody @Valid CreateProposalFromDraftDto createProposalFromDraftDto) {
        draftService.createProposalFromDraft(createProposalFromDraftDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Редактировать черновик.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{draftId}")
    public ResponseEntity<Void> updateDraft(@PathVariable UUID draftId,
                                            @RequestBody @Valid UpdateDraftDto updateDraftDto) {
        draftService.updateDraft(draftId, updateDraftDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Удалить черновик.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{draftId}")
    public ResponseEntity<Void> deleteDraft(@PathVariable UUID draftId) {
        draftService.deleteDraft(draftId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Получить список предложений-черновиков.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/proposals")
    public ResponseEntity<List<DraftDto>> getProposalDrafts() {
        List<DraftDto> drafts = draftService.getProposalDrafts();
        return new ResponseEntity<>(drafts, HttpStatus.OK);
    }

}
