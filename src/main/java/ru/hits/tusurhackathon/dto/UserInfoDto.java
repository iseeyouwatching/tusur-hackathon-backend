package ru.hits.tusurhackathon.dto;

import lombok.*;
import ru.hits.tusurhackathon.entity.UserEntity;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserInfoDto {

    private UUID id;

    private String username;

    private String firstName;

    private String lastName;

    private String middleName;

    private String phone;

    private int avatarNumber;

    public UserInfoDto(UserEntity user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.middleName = user.getMiddleName();
        this.phone = user.getPhone();
        this.avatarNumber = user.getAvatarNumber();
    }

}
