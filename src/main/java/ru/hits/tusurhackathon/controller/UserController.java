package ru.hits.tusurhackathon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.tusurhackathon.dto.ChangeAvatarDto;
import ru.hits.tusurhackathon.dto.UserInfoDto;
import ru.hits.tusurhackathon.service.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Пользователи.")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Получить информацию о себе.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/user-info")
    public ResponseEntity<UserInfoDto> getUserInfo() {
        return new ResponseEntity<>(userService.getUserInfo(), HttpStatus.OK);
    }

    @Operation(
            summary = "Поменять аватарку.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/change-avatar")
    public ResponseEntity<Void> changeAvatar(@RequestBody ChangeAvatarDto changeAvatarDto) {
        userService.changeAvatar(changeAvatarDto.getAvatarNumber());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Получить список всех пользователей.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<List<UserInfoDto>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @Operation(
            summary = "Получить список пользователей, которые не находятся в указанном проекте.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/not-in-project/{projectId}")
    public ResponseEntity<List<UserInfoDto>> getUsersNotInProject(@PathVariable UUID projectId) {
        return new ResponseEntity<>(userService.getUsersNotInProject(projectId), HttpStatus.OK);
    }

}
