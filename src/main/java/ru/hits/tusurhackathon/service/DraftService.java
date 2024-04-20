package ru.hits.tusurhackathon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.tusurhackathon.dto.*;
import ru.hits.tusurhackathon.entity.*;
import ru.hits.tusurhackathon.enumeration.DraftType;
import ru.hits.tusurhackathon.enumeration.ProposalStatus;
import ru.hits.tusurhackathon.exception.NotFoundException;
import ru.hits.tusurhackathon.repository.*;
import ru.hits.tusurhackathon.security.JwtUserData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DraftService {

    private final UserRepository userRepository;

    private final DraftRepository draftRepository;

    private final ProjectRepository projectRepository;

    private final ProposalRepository proposalRepository;

    private final CommentRepository commentRepository;

    @Transactional
    public void createProposalDraft(CreateDraftDto createDraftDto) {
        UUID authenticatedUserId = getAuthenticatedUserId();

        UserEntity user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new NotFoundException("Пользователя с ID " + authenticatedUserId + " не существует"));

        ProjectEntity project = projectRepository.findById(createDraftDto.getProjectId())
                .orElseThrow(() -> new NotFoundException("Проект с ID " + createDraftDto.getProjectId() + " не найден"));

        DraftEntity draft = DraftEntity.builder()
                .content(createDraftDto.getContent())
                .jiraLink(createDraftDto.getJiraLink())
                .user(user)
                .draftType(DraftType.PROPOSAL)
                .project(project)
                .savedAt(LocalDateTime.now())
                .build();

        draftRepository.save(draft);
    }

    @Transactional
    public void createCommentDraft(CreateCommentDraftDto createCommentDraftDto) {
        UUID authenticatedUserId = getAuthenticatedUserId();

        UserEntity user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new NotFoundException("Пользователя с ID " + authenticatedUserId + " не существует"));

        if (createCommentDraftDto.getCommentId() != null) {
            CommentEntity comment = commentRepository.findById(createCommentDraftDto.getCommentId())
                    .orElseThrow(() -> new NotFoundException("Комментарий ID " + createCommentDraftDto.getCommentId() + " не найден"));

            DraftEntity draft = DraftEntity.builder()
                    .content(createCommentDraftDto.getText())
                    .user(user)
                    .draftType(DraftType.COMMENT)
                    .comment(comment)
                    .project(comment.getProposal().getProject())
                    .savedAt(LocalDateTime.now())
                    .build();

            draftRepository.save(draft);
        }

        if (createCommentDraftDto.getProposalId() != null) {
            ProposalEntity proposal = proposalRepository.findById(createCommentDraftDto.getProposalId())
                    .orElseThrow(() -> new NotFoundException("Предложение с ID " + createCommentDraftDto.getProposalId() + " не найдено"));

            DraftEntity draft = DraftEntity.builder()
                    .content(createCommentDraftDto.getText())
                    .user(user)
                    .draftType(DraftType.COMMENT)
                    .proposal(proposal)
                    .project(proposal.getProject())
                    .savedAt(LocalDateTime.now())
                    .build();

            draftRepository.save(draft);
        }

    }

    @Transactional
    public void createProposalFromDraft(CreateProposalFromDraftDto createProposalFromDraftDto) {
        UUID authenticatedUserId = getAuthenticatedUserId();

        UserEntity user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new NotFoundException("Пользователя с ID " + authenticatedUserId + " не существует"));

        DraftEntity draft = draftRepository.findById(createProposalFromDraftDto.getDraftId())
                .orElseThrow(() -> new NotFoundException("Черновика с ID " + createProposalFromDraftDto.getDraftId() + " не существует"));

        ProposalEntity proposal = ProposalEntity.builder()
                .text(createProposalFromDraftDto.getContent())
                .jiraLink(createProposalFromDraftDto.getJiraLink())
                .votesFor(0)
                .votesAgainst(0)
                .user(user)
                .proposalStatus(ProposalStatus.NEW)
                .project(draft.getProject())
                .createdAt(LocalDateTime.now())
                .build();

        proposalRepository.save(proposal);
        draftRepository.delete(draft);
    }

    @Transactional
    public void updateDraft(UUID draftId, UpdateDraftDto updateDraftDto) {
        DraftEntity draft = draftRepository.findById(draftId)
                .orElseThrow(() -> new NotFoundException("Черновика с ID " + draftId + " не существует"));

        if (updateDraftDto.getContent() != null) {
            draft.setContent(updateDraftDto.getContent());
        }

        if (updateDraftDto.getJiraLink() != null) {
            draft.setJiraLink(updateDraftDto.getJiraLink());
        }

        draftRepository.save(draft);
    }

    @Transactional
    public void deleteDraft(UUID draftId) {
        DraftEntity draft = draftRepository.findById(draftId)
                .orElseThrow(() -> new NotFoundException("Черновика с ID " + draftId + " не существует"));

        draftRepository.delete(draft);
    }

    public List<DraftDto> getProposalDrafts() {
        UUID authenticatedUserId = getAuthenticatedUserId();

        UserEntity user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new NotFoundException("Пользователя с ID " + authenticatedUserId + " не существует"));

        List<DraftEntity> draftEntities = draftRepository.findAllByUserAndDraftType(user, DraftType.PROPOSAL);
        return draftEntities.stream()
                .map(this::mapToDraftDto)
                .collect(Collectors.toList());
    }

    private DraftDto mapToDraftDto(DraftEntity draftEntity) {
        return DraftDto.builder()
                .id(draftEntity.getId())
                .content(draftEntity.getContent())
                .jiraLink(draftEntity.getJiraLink())
                .build();
    }

    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        return userData.getId();
    }

}
