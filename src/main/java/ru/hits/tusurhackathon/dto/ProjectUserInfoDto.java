package ru.hits.tusurhackathon.dto;

import lombok.*;
import ru.hits.tusurhackathon.entity.UserEntity;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProjectUserInfoDto {

    private UUID id;

    private String firstName;

    private String lastName;

    private String middleName;

    private String phone;

    private Integer avatarNumber;

    private Integer availableVotes;

    private Boolean isAdmin;

    public ProjectUserInfoDto(UserEntity user, Integer availableVotes, Boolean isAdmin) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.middleName = user.getMiddleName();
        this.phone = user.getPhone();
        this.avatarNumber = user.getAvatarNumber();
        this.availableVotes = availableVotes;
        this.isAdmin = isAdmin;
    }

}
