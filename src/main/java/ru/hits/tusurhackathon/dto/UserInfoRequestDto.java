package ru.hits.tusurhackathon.dto;

import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserInfoRequestDto {

    private Integer id;

    private String name;

    private String surname;

    private String patronymic;

    private String phone;

}
