package ru.hits.tusurhackathon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.tusurhackathon.dto.CreateCommentDto;
import ru.hits.tusurhackathon.dto.EditCommentDto;
import ru.hits.tusurhackathon.entity.CommentEntity;
import ru.hits.tusurhackathon.entity.DraftEntity;
import ru.hits.tusurhackathon.entity.ProposalEntity;
import ru.hits.tusurhackathon.entity.UserEntity;
import ru.hits.tusurhackathon.enumeration.DraftType;
import ru.hits.tusurhackathon.exception.ForbiddenException;
import ru.hits.tusurhackathon.exception.NotFoundException;
import ru.hits.tusurhackathon.repository.CommentRepository;
import ru.hits.tusurhackathon.repository.DraftRepository;
import ru.hits.tusurhackathon.repository.ProposalRepository;
import ru.hits.tusurhackathon.repository.UserRepository;
import ru.hits.tusurhackathon.security.JwtUserData;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final ProposalRepository proposalRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final DraftRepository draftRepository;

    @Transactional
    public void addCommentToProposal(UUID proposalId, CreateCommentDto createCommentDto) {
        ProposalEntity proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new NotFoundException("Предложение с ID " + proposalId + " не найдено"));

        UUID authenticatedUserId = getAuthenticatedUserId();

        UserEntity user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new NotFoundException("Пользователя с ID " + authenticatedUserId + " не существует"));

        CommentEntity comment = CommentEntity.builder()
                .text(createCommentDto.getText())
                .user(user)
                .proposal(proposal)
                .createdAt(LocalDateTime.now())
                .build();

        Optional<DraftEntity> draft = draftRepository.findByUserAndProposalAndDraftType(user, proposal, DraftType.COMMENT);

        if (draft.isPresent()) {
            draftRepository.delete(draft.get());
        }

        commentRepository.save(comment);
    }

    @Transactional
    public void addReplyToComment(UUID commentId, CreateCommentDto createCommentDto) {
        CommentEntity parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с ID " + commentId + " не найден"));

        UUID authenticatedUserId = getAuthenticatedUserId();

        UserEntity user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new NotFoundException("Пользователя с ID " + authenticatedUserId + " не существует"));

        CommentEntity reply = CommentEntity.builder()
                .text(createCommentDto.getText())
                .user(user)
                .parentComment(parentComment)
                .proposal(parentComment.getProposal())
                .createdAt(LocalDateTime.now())
                .build();

        parentComment.getReplies().add(reply);

        Optional<DraftEntity> draft = draftRepository.findByUserAndCommentAndDraftType(user, parentComment, DraftType.COMMENT);

        if (draft.isPresent()) {
            draftRepository.delete(draft.get());
        }

        commentRepository.save(reply);
    }

    @Transactional
    public void editComment(UUID commentId, EditCommentDto editCommentDto) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с ID " + commentId + " не найден"));

        UUID authenticatedUserId = getAuthenticatedUserId();

        // Проверяем, имеет ли текущий пользователь право редактировать комментарий
        if (!comment.getUser().getId().equals(authenticatedUserId)) {
            throw new ForbiddenException("Вы не имеете права редактировать этот комментарий");
        }

        // Обновляем текст комментария
        comment.setText(editCommentDto.getText());

        commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(UUID commentId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с ID " + commentId + " не найден"));

        UUID authenticatedUserId = getAuthenticatedUserId();

        UserEntity user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new NotFoundException("Пользователя с ID " + authenticatedUserId + " не существует"));

        // Проверяем, имеет ли пользователь право на удаление комментария
        if (!comment.getUser().equals(user)) {
            throw new ForbiddenException("У вас нет прав на удаление этого комментария");
        }

        commentRepository.delete(comment);
    }

    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        return userData.getId();
    }

}
