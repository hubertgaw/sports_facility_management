package pl.lodz.hubertgaw.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class ClimbingWall extends SportObject {

    @NotNull(message = "capacity cannot be null")
    private Integer capacity;

    @NotNull(message = "single price cannot be null")
    private Double singlePrice;
}
