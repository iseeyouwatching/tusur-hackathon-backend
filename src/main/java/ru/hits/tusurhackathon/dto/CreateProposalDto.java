package ru.hits.tusurhackathon.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateProposalDto {

    @NotBlank(message = "Текст предложения является обязательным к заполнению")
    private String text;

    private UUID projectId;

}
