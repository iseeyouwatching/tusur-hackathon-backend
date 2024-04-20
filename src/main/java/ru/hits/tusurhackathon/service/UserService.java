package ru.hits.tusurhackathon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.tusurhackathon.dto.ProposalInListDto;
import ru.hits.tusurhackathon.dto.UserInfoDto;
import ru.hits.tusurhackathon.entity.ProjectEntity;
import ru.hits.tusurhackathon.entity.UserEntity;
import ru.hits.tusurhackathon.exception.NotFoundException;
import ru.hits.tusurhackathon.repository.ProjectRepository;
import ru.hits.tusurhackathon.repository.UserRepository;
import ru.hits.tusurhackathon.security.JwtUserData;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public UserInfoDto getUserInfo() {
        UUID authenticatedUserId = getAuthenticatedUserId();

        UserEntity user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new NotFoundException("Пользователя с ID " + authenticatedUserId + " не существует"));

        return new UserInfoDto(user);
    }

    @Transactional
    public void changeAvatar(int avatarNumber) {
        UUID authenticatedUserId = getAuthenticatedUserId();

        UserEntity user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new NotFoundException("Пользователя с ID " + authenticatedUserId + " не существует"));

        user.setAvatarNumber(avatarNumber);
        userRepository.save(user);
    }

    public List<UserInfoDto> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();

        List<UserInfoDto> userInfoDtos = users.stream()
                .map(UserInfoDto::new)
                .collect(Collectors.toList());

        return userInfoDtos;
    }

    public List<UserInfoDto> getUsersNotInProject(UUID projectId) {
        // Получение проекта по его ID
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Проект с ID " + projectId + " не найден"));

        // Получение всех пользователей
        List<UserEntity> allUsers = userRepository.findAll();

        // Получение пользователей, находящихся в указанном проекте
        List<UserEntity> projectUsers = project.getUsers();

        // Фильтрация пользователей, чтобы оставить только тех, кто не включен в проект
        List<UserEntity> usersNotInProject = allUsers.stream()
                .filter(user -> !projectUsers.contains(user))
                .collect(Collectors.toList());

        // Преобразование в список DTO
        return usersNotInProject.stream()
                .map(UserInfoDto::new)
                .collect(Collectors.toList());
    }

    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        return userData.getId();
    }

}
