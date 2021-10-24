package pl.lodz.hubertgaw.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

public abstract class SportObject {

    @NotBlank
    private String name;

    @NotNull
    private Double fullPrice;

    private Set<RentEquipment> rentEquipments;

}
