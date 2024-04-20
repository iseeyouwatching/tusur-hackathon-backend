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

    private String firstName;

    private String lastName;

    private String middleName;

    private String phone;

    private Integer avatarNumber;

    public UserInfoDto(UserEntity user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.middleName = user.getMiddleName();
        this.phone = user.getPhone();
        this.avatarNumber = user.getAvatarNumber();
    }

    public UserInfoDto(UserInfoRequestDto userInfoRequestDto) {
        this.id = UUID.randomUUID();
        this.firstName = userInfoRequestDto.getName();
        this.lastName = userInfoRequestDto.getSurname();
        this.middleName = userInfoRequestDto.getPatronymic();
        this.phone = userInfoRequestDto.getPhone();
        this.avatarNumber = null;
    }

}
