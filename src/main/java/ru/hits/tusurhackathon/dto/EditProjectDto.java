package ru.hits.tusurhackathon.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EditProjectDto {

    private String name = null;

    private String schedule = null;

    private Integer votesPerPeriod = null;

    private Integer votesRefreshPeriodDays = null;

}
