package pl.lodz.hubertgaw.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class DartRoom extends SportObject {

    @NotNull
    private Integer standsNumber;

    @NotNull
    private Double standPrice;
}
