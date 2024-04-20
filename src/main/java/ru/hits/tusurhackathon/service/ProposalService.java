package ru.hits.tusurhackathon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.tusurhackathon.comparator.ProposalComparator;
import ru.hits.tusurhackathon.dto.*;
import ru.hits.tusurhackathon.entity.*;
import ru.hits.tusurhackathon.enumeration.ProposalStatus;
import ru.hits.tusurhackathon.exception.ConflictException;
import ru.hits.tusurhackathon.exception.ForbiddenException;
import ru.hits.tusurhackathon.exception.NotFoundException;
import ru.hits.tusurhackathon.repository.*;
import ru.hits.tusurhackathon.security.JwtUserData;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProposalService {

    private final ProposalRepository proposalRepository;

    private final UserRepository userRepository;

    private final UserVoteRepository userVoteRepository;

    private final ProjectUserRepository projectUserRepository;

    private final ProjectRepository projectRepository;

    public ProposalInfoDto getProposal(UUID id) {
        UUID authenticatedUserId = getAuthenticatedUserId();
        UserEntity user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + authenticatedUserId + " не существует"));

        ProposalEntity proposal = proposalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Предложение с ID " + id + " не найдено"));

        ProjectUserEntity projectUser = projectUserRepository.findByUserAndProject(user, proposal.getProject())
                .orElseThrow(() -> new NotFoundException("Запись о пользователе с ID " + authenticatedUserId + " в проекте с ID " + proposal.getProject().getId() + " не найдена"));
        boolean canVote = projectUser.getNumberOfVotes() < proposal.getProject().getVotesPerPeriod();

        List<CommentEntity> comments = proposal.getComments();

        Optional<UserVoteEntity> existingVote = userVoteRepository.findByUserAndProposal(user, proposal);
        Boolean userVote;
        if (existingVote.isPresent()) {
            userVote = existingVote.get().getIsUpvote();

            ProposalInfoDto proposalInfoDto = ProposalInfoDto.builder()
                    .id(proposal.getId())
                    .text(proposal.getText())
                    .user(new UserInfoDto(proposal.getUser()))
                    .proposalStatus(proposal.getProposalStatus())
                    .userVote(userVote) // Здесь нужно узнать голос пользователя и установить соответствующее значение
                    .canVote(canVote)
                    .comments(comments)
                    .jiraLink(proposal.getJiraLink())
//                .draftComment(null) // Значение комментария-черновика необходимо получить из соответствующего поля предложения
                    .canBeVoteCanceled(existingVote.get().getCanBeVoiceCanceled()) // Значение canBeVoteCanceled можно установить, основываясь на каких-то условиях
                    .build();

            return proposalInfoDto;
        } else {
            ProposalInfoDto proposalInfoDto = ProposalInfoDto.builder()
                    .id(proposal.getId())
                    .text(proposal.getText())
                    .user(new UserInfoDto(proposal.getUser()))
                    .proposalStatus(proposal.getProposalStatus())
                    .userVote(null) // Здесь нужно узнать голос пользователя и установить соответствующее значение
                    .canVote(canVote)
                    .comments(comments)
                    .jiraLink(proposal.getJiraLink())
//                .draftComment(null) // Значение комментария-черновика необходимо получить из соответствующего поля предложения
                    .canBeVoteCanceled(true) // Значение canBeVoteCanceled можно установить, основываясь на каких-то условиях
                    .build();

            return proposalInfoDto;
        }
    }

    @Transactional
    public ProposalDto createProposal(CreateProposalDto createProposalDto) {
        UUID authenticatedUserId = getAuthenticatedUserId();
        UserEntity user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + authenticatedUserId + " не существует"));

        ProjectEntity project = projectRepository.findById(createProposalDto.getProjectId())
                .orElseThrow(() -> new NotFoundException("Проект с ID " + createProposalDto.getProjectId() + " не найден"));

        ProposalEntity proposal = ProposalEntity.builder()
                .text(createProposalDto.getText())
                .votesFor(0)
                .votesAgainst(0)
                .user(user)
                .proposalStatus(ProposalStatus.NEW)
                .comments(Collections.emptyList())
                .project(project)
                .createdAt(LocalDateTime.now())
                .build();

        proposal = proposalRepository.save(proposal);

        return new ProposalDto(proposal);
    }

    @Transactional
    public ProposalInfoDto editProposal(UUID id, EditProposalDto editProposalDto) {
        UUID authenticatedUserId = getAuthenticatedUserId();

        UserEntity user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new NotFoundException("Пользователя с ID " + authenticatedUserId + " не существует"));

        // Получение проекта по его ID
        ProposalEntity proposal = proposalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Предложение с ID " + id + " не найдено"));

        if (user != proposal.getUser()) {
            throw new ForbiddenException("Только создатель может менять предложение");
        }

        if (editProposalDto.getText() != null) {
            proposal.setText(editProposalDto.getText());
        }

        if (editProposalDto.getJiraLink() != null) {
            proposal.setJiraLink(editProposalDto.getJiraLink());
        }

        // Сохранение обновленного проекта в базе данных
        proposal = proposalRepository.save(proposal);

        final ProjectEntity project = proposal.getProject();
        ProjectUserEntity projectUser = projectUserRepository.findByUserAndProject(user, proposal.getProject())
                .orElseThrow(() -> new NotFoundException("Запись о пользователе с ID " + authenticatedUserId + " в проекте с ID " + project.getId() + " не найдена"));
        boolean canVote = projectUser.getNumberOfVotes() < proposal.getProject().getVotesPerPeriod();

        Optional<UserVoteEntity> existingVote = userVoteRepository.findByUserAndProposal(user, proposal);
        Boolean userVote;
        if (existingVote.isPresent()) {
            userVote = existingVote.get().getIsUpvote();
            return new ProposalInfoDto(proposal, userVote, canVote);
        } else {
            return new ProposalInfoDto(proposal, null, canVote);
        }
    }

    @Transactional
    public void voteForOrAgainstProposal(UUID proposalId, Boolean isUpvote) {
        ProposalEntity proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new NotFoundException("Предложение с ID " + proposalId + " не найдено"));

        UUID authenticatedUserId = getAuthenticatedUserId();
        UserEntity user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + authenticatedUserId + " не существует"));

        if (proposal.getProposalStatus() != ProposalStatus.NEW) {
            throw new ConflictException("Голосовать можно только за предложения в статусе НОВЫЙ");
        }

        Optional<UserVoteEntity> existingVote = userVoteRepository.findByUserAndProposal(user, proposal);

        ProjectUserEntity projectUser = projectUserRepository.findByUserAndProject(user, proposal.getProject())
                .orElseThrow(() -> new NotFoundException("Запись о пользователе с ID " + authenticatedUserId + " в проекте с ID " + proposal.getProject().getId() + " не найдена"));

        if (projectUser.getNumberOfVotes() >= proposal.getProject().getVotesPerPeriod()) {
            throw new ForbiddenException("У вас больше нет доступных голосов в рамках этого проекта");
        }

        if (existingVote.isPresent() && existingVote.get().getIsUpvote() == isUpvote) {
            throw new ForbiddenException("Вы уже проголосовали " + (isUpvote ? "за данное предложение" : "против данного предложения"));
        }

        if (existingVote.isPresent()) {
            projectUser.setNumberOfVotes(projectUser.getNumberOfVotes() - 1);

            existingVote.get().setIsUpvote(isUpvote);
            userVoteRepository.save(existingVote.get());
        } else {
            UserVoteEntity userVote = UserVoteEntity.builder()
                    .user(user)
                    .proposal(proposal)
                    .isUpvote(isUpvote)
                    .build();
            userVoteRepository.save(userVote);
        }

        if (isUpvote) {
            if (proposal.getVotesAgainst() != 0) {
                proposal.setVotesAgainst(proposal.getVotesAgainst() - 1);
            }
            proposal.setVotesFor(proposal.getVotesFor() + 1);
        } else {
            if (proposal.getVotesFor() != 0) {
                proposal.setVotesFor(proposal.getVotesFor() - 1);
            }
            proposal.setVotesAgainst(proposal.getVotesAgainst() + 1);
        }
        proposalRepository.save(proposal);

        projectUser.setNumberOfVotes(projectUser.getNumberOfVotes() + 1);
        projectUserRepository.save(projectUser);
    }

    @Transactional
    public void cancelVote(UUID proposalId) {
        ProposalEntity proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new NotFoundException("Предложение с ID " + proposalId + " не найдено"));

        UUID authenticatedUserId = getAuthenticatedUserId();
        UserEntity user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + authenticatedUserId + " не существует"));

        UserVoteEntity existingVote = userVoteRepository.findByUserAndProposal(user, proposal)
                .orElseThrow(() -> new NotFoundException("Вы еще не голосовали за это предложение"));

        if (existingVote.getIsUpvote()) {
            proposal.setVotesFor(proposal.getVotesFor() - 1);
        } else {
            proposal.setVotesAgainst(proposal.getVotesAgainst() - 1);
        }

        // Удаляем запись о голосе пользователя
        userVoteRepository.delete(existingVote);

        proposalRepository.save(proposal);

        ProjectUserEntity projectUser = projectUserRepository.findByUserAndProject(user, proposal.getProject())
                .orElseThrow(() -> new NotFoundException("Запись о пользователе с ID " + authenticatedUserId + " в проекте с ID " + proposal.getProject().getId() + " не найдена"));

        projectUser.setNumberOfVotes(projectUser.getNumberOfVotes() - 1);
        projectUserRepository.save(projectUser);
    }

    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        return userData.getId();
    }

}
