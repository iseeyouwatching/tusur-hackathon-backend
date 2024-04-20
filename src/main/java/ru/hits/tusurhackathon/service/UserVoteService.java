//package ru.hits.tusurhackathon.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import ru.hits.tusurhackathon.entity.ProjectEntity;
//import ru.hits.tusurhackathon.entity.UserEntity;
//import ru.hits.tusurhackathon.repository.ProjectRepository;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class UserVoteService {
//    private final ProjectService projectService;
//
//    private final ProjectRepository projectRepository;
//
//    private final UserService userService;
//
//    @Scheduled(cron = "#{@projectService.getCronExpressionForProject(project)}")
//    public void updateVotesCount() {
//        List<ProjectEntity> projects = projectRepository.findAll();
//        for (ProjectEntity project : projects) {
//            int votesPerPeriod = project.getVotesPerPeriod();
//            List<UserEntity> users = userService.getUsersByProject(project);
//            for (UserEntity user : users) {
//                user.setVotesCount(votesPerPeriod);
//            }
//            userService.saveAll(users);
//        }
//    }
//}
