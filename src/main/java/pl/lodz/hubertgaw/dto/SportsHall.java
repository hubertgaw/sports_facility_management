package pl.lodz.hubertgaw.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class SportsHall extends SportObject {

    @NotNull
    private Integer sectorsNumber;

    @NotNull
    private Double sectorPrice;
}
