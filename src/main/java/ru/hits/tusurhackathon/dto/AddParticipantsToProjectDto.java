package ru.hits.tusurhackathon.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddParticipantsToProjectDto {

    private List<UUID> userIds;

}
