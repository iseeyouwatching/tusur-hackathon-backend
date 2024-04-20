package ru.hits.tusurhackathon.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TempTokenDto {

    @JsonProperty("temp_token")
    private String tempToken;

}
