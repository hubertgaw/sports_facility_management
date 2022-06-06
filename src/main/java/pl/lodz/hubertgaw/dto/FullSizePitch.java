package pl.lodz.hubertgaw.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class FullSizePitch extends SportObject {

    @NotNull(message = "half pitch price cannot be null")
    private Double halfPitchPrice;

    @NotNull(message = "information about if pitch is half rentable must be provided")
    @Getter(AccessLevel.NONE)
    private Boolean isHalfRentable;

    @Override
    public Boolean getIsHalfRentable() {
        return this.isHalfRentable;
    }
}
