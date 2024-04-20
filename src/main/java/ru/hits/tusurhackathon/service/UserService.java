package ru.hits.tusurhackathon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.hits.tusurhackathon.dto.*;
import ru.hits.tusurhackathon.entity.BlacklistTokenEntity;
import ru.hits.tusurhackathon.entity.ProjectEntity;
import ru.hits.tusurhackathon.entity.UserEntity;
import ru.hits.tusurhackathon.exception.NotFoundException;
import ru.hits.tusurhackathon.repository.BlacklistTokenRepository;
import ru.hits.tusurhackathon.repository.ProjectRepository;
import ru.hits.tusurhackathon.repository.UserRepository;
import ru.hits.tusurhackathon.security.JWTUtil;
import ru.hits.tusurhackathon.security.JwtUserData;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final ProjectRepository projectRepository;

    private final BlacklistTokenRepository blacklistTokenRepository;

    private final JWTUtil jwtUtil;

    private static final String API_URL = "http://79.174.91.149/api/temp_token_auth";

    public AccessTokenDto signIn(TempTokenDto tempTokenDto) {
        RestTemplate restTemplate = new RestTemplate();

        // Установка заголовков HTTP
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Создание объекта HttpEntity с телом запроса и заголовками
        HttpEntity<TempTokenDto> requestEntity = new HttpEntity<>(tempTokenDto, headers);

        // Отправка POST запроса и получение ответа
        ResponseEntity<UserInfoRequestDto> responseEntity = restTemplate.exchange(
                API_URL,
                HttpMethod.POST,
                requestEntity,
                UserInfoRequestDto.class
        );

        // Получение тела ответа из ResponseEntity
        UserInfoRequestDto userInfoRequestDto = responseEntity.getBody();

        Optional<UserEntity> user = userRepository.findByPhone(userInfoRequestDto.getPhone());

        if (user.isPresent()) {
            return new AccessTokenDto(jwtUtil.generateToken(user.get().getId()));
        } else {
            UserEntity newUser = UserEntity.builder()
                    .firstName(userInfoRequestDto.getName())
                    .lastName(userInfoRequestDto.getSurname())
                    .middleName(userInfoRequestDto.getPatronymic())
                    .phone(userInfoRequestDto.getPhone())
                    .build();

            newUser = userRepository.save(newUser);

            return new AccessTokenDto(jwtUtil.generateToken(newUser.getId()));
        }
    }

    @Transactional
    public void userLogOut() {
        String bearerToken = getBearerTokenHeader().substring(7);

        BlacklistTokenEntity blacklistToken = BlacklistTokenEntity.builder()
                .token(bearerToken)
                .build();
        blacklistTokenRepository.save(blacklistToken);
    }

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

    private static String getBearerTokenHeader() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization");
    }

}
