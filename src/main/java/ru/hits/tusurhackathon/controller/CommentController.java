package ru.hits.tusurhackathon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.tusurhackathon.dto.CreateCommentDto;
import ru.hits.tusurhackathon.dto.EditCommentDto;
import ru.hits.tusurhackathon.service.CommentService;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Комментарии.")
public class CommentController {

    private final CommentService commentService;

    @Operation(
            summary = "Оставить комментарий к предложению.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/proposals/{proposalId}")
    public ResponseEntity<Void> addCommentToProposal(@PathVariable UUID proposalId,
                                                     @RequestBody @Valid CreateCommentDto createCommentDto) {
        commentService.addCommentToProposal(proposalId, createCommentDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Оставить комментарий к комментарию.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/{commentId}/replies")
    public ResponseEntity<Void> addReplyToComment(@PathVariable UUID commentId,
                                                    @RequestBody @Valid CreateCommentDto createCommentDto) {
        commentService.addReplyToComment(commentId, createCommentDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Редактировать комментарий.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{commentId}")
    public ResponseEntity<Void> editComment(@PathVariable UUID commentId,
                                         @RequestBody @Valid EditCommentDto editCommentDto) {
        commentService.editComment(commentId, editCommentDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Удалить комментарий.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId) {
        commentService.deleteComment(commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
