package ru.hits.tusurhackathon.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateProjectDto {

    private String name;
    private String schedule;
    private Integer votesPerPeriod;
    private Integer votesRefreshPeriodDays;

}
