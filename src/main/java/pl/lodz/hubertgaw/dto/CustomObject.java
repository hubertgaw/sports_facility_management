package pl.lodz.hubertgaw.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomObject extends SportObject {

    @NotNull(message = "type cannot be null")
    private String type;

    @Getter(AccessLevel.NONE)
    private Integer capacity;

    private Double singlePrice;

    @Getter(AccessLevel.NONE)
    private Boolean isHalfRentable;

    @Override
    public Integer getCapacity() {
        if (null == this.capacity || this.capacity == 0) {
            return null;
        }
        return this.capacity;
    }

    @Override
    public Boolean getIsHalfRentable() {
        if (null == this.isHalfRentable || !this.isHalfRentable) {
            return false;
        }
        return true;
    }
}
