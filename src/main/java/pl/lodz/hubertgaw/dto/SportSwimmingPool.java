package pl.lodz.hubertgaw.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class SportSwimmingPool extends SportObject {

    @NotNull(message = "tracks number cannot be null")
    private Integer tracksNumber;

    @NotNull(message = "track price cannot be null")
    private Double trackPrice;

    @Override
    public Integer getCapacity() {
        return this.tracksNumber;
    }
}
