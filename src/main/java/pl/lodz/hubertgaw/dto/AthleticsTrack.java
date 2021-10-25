package pl.lodz.hubertgaw.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class AthleticsTrack extends SportObject {

    @NotNull
    private Integer capacity;

    @NotNull
    private Double singleTrackPrice;
}