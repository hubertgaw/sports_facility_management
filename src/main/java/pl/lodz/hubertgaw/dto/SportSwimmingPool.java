package pl.lodz.hubertgaw.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class SportSwimmingPool extends SportObject {

    @NotNull
    private Integer tracksNumber;

    @NotNull
    private Double trackPrice;
}
