package pl.lodz.hubertgaw.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class RentEquipment {

    @NotBlank
    private String name;

    @NotNull
    private Double price;

    private Set<String> sportObjects;
}
