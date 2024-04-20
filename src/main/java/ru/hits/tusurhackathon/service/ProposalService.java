package ru.hits.tusurhackathon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.tusurhackathon.dto.CreateProposalDto;
import ru.hits.tusurhackathon.dto.ProposalDto;
import ru.hits.tusurhackathon.dto.ProposalInListDto;
import ru.hits.tusurhackathon.entity.*;
import ru.hits.tusurhackathon.enumeration.ProposalStatus;
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

    public List<ProposalInListDto> getProposals() {
        List<ProposalEntity> proposalEntities = proposalRepository.findAllByOrderByVotesForDesc();

        List<ProposalInListDto> proposalInListDtos = proposalEntities.stream()
                .map(ProposalInListDto::new)
                .collect(Collectors.toList());

        return proposalInListDtos;
    }

    @Transactional
    public ProposalDto createProposal(CreateProposalDto createProposalDto) {
        UUID authenticatedUserId = getAuthenticatedUserId();

        UserEntity user = UserEntity.builder()
                .id(authenticatedUserId)
                .build();

        user = userRepository.save(user);

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
    public void voteForOrAgainstProposal(UUID proposalId, Boolean isUpvote) {
        ProposalEntity proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new NotFoundException("Предложение с ID " + proposalId + " не найдено"));

        UUID authenticatedUserId = getAuthenticatedUserId();
        UserEntity user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + authenticatedUserId + " не существует"));

        Optional<UserVoteEntity> existingVote = userVoteRepository.findByUserAndProposal(user, proposal);

        ProjectUserEntity projectUser = projectUserRepository.findByUserAndProject(user, proposal.getProject())
                .orElseThrow(() -> new NotFoundException("Запись о пользователе с ID " + authenticatedUserId + " в проекте с ID " + proposal.getProject().getId() + " не найдена"));

        if (projectUser.getNumberOfVotes() >= proposal.getProject().getVotesPerPeriod()) {
            throw new ForbiddenException("У вас больше нет доступных голосов в рамках этого проекта");
        }

        if (existingVote.isPresent() && existingVote.get().isUpvote() == isUpvote) {
            throw new ForbiddenException("Вы уже проголосовали " + (isUpvote ? "за данное предложение" : "против данного предложения"));
        }

        if (existingVote.isPresent()) {
            existingVote.get().setUpvote(isUpvote);
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

        if (existingVote.isUpvote()) {
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
