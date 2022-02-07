package pl.lodz.hubertgaw.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class FullSizePitch extends SportObject {

    @NotNull(message = "half pitch price cannot be null")
    private Double halfPitchPrice;

    private Boolean isFullRented;
}
