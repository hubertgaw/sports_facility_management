package pl.lodz.hubertgaw.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class AthleticsTrack extends SportObject {

    @NotNull(message = "capacity cannot be null")
    private Integer capacity;

    @NotNull(message = "single track price cannot be null")
    private Double singleTrackPrice;
}
