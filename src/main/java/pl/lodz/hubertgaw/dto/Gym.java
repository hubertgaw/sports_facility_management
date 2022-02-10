package pl.lodz.hubertgaw.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class Gym extends SportObject {

    @NotNull(message = "capacity cannot be null")
    @Getter(AccessLevel.NONE)
    private Integer capacity;

    @NotNull(message = "single price cannot be null")
    private Double singlePrice;

    @Override
    public Integer getCapacity() {
        return this.capacity;
    }
}
