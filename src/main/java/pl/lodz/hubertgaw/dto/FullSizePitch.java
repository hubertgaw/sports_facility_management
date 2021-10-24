package pl.lodz.hubertgaw.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class FullSizePitch extends SportObject {

    @NotNull
    private Double halfPitchPrice;

    private Boolean isFullRented;
}
