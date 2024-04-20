package ru.hits.tusurhackathon.schedular;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import ru.hits.tusurhackathon.entity.ProjectEntity;
import ru.hits.tusurhackathon.entity.ProjectUserEntity;
import ru.hits.tusurhackathon.entity.ProposalEntity;
import ru.hits.tusurhackathon.entity.UserVoteEntity;
import ru.hits.tusurhackathon.repository.ProjectRepository;
import ru.hits.tusurhackathon.repository.ProjectUserRepository;
import ru.hits.tusurhackathon.repository.ProposalRepository;
import ru.hits.tusurhackathon.repository.UserVoteRepository;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class MyScheduler {

    private final ProjectRepository projectRepository;
    private final ProjectUserRepository projectUserRepository;
    private final UserVoteRepository userVoteRepository;
    private final ProposalRepository proposalRepository;

    @Scheduled(fixedRate = 1209600000)
    public void invokeTask() {
        List<ProjectEntity> projects = projectRepository.findAll();
        for (ProjectEntity project : projects) {
            List<ProjectUserEntity> projectUsers = projectUserRepository.findAllByProject(project);
            for (ProjectUserEntity projectUser: projectUsers) {
                projectUser.setNumberOfVotes(0);
                projectUserRepository.save(projectUser);
            }

            List<ProposalEntity> proposals = proposalRepository.findAllByProject(project);
            for (ProposalEntity proposal: proposals) {
                List<UserVoteEntity> userVotes = userVoteRepository.findAllByProposal(proposal);
                for (UserVoteEntity userVote: userVotes) {
                    userVote.setCanBeVoiceCanceled(false);
                    userVoteRepository.save(userVote);
                }
            }
        }
        log.info("Test...");
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler(){
        ThreadPoolTaskScheduler threadPoolTaskScheduler
                = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(100);
        threadPoolTaskScheduler.setThreadNamePrefix(
                "SomePrefix");
        return threadPoolTaskScheduler;
    }

}
