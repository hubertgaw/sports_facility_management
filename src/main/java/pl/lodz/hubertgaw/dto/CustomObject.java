package pl.lodz.hubertgaw.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomObject extends SportObject {

    @NotNull(message = "type cannot be null")
    private String type;

    private Integer capacity;

    private Double singlePrice;

    private Integer standsNumber;

}
