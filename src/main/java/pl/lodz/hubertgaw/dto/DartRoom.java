package pl.lodz.hubertgaw.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class DartRoom extends SportObject {

    @NotNull(message = "stands number cannot be null")
    private Integer standsNumber;

    @NotNull(message = "stand price cannot be null")
    private Double standPrice;

    @Override
    public Integer getCapacity() {
        return this.standsNumber;
    }
}
