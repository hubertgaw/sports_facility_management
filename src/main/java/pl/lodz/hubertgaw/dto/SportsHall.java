package pl.lodz.hubertgaw.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class SportsHall extends SportObject {

    @NotNull(message = "sectors number cannot be null")
    private Integer sectorsNumber;

    @NotNull(message = "sector price cannot be null")
    private Double sectorPrice;

    @Override
    public Integer getCapacity() {
        return this.sectorsNumber;
    }
}
