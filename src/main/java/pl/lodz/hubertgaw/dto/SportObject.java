package pl.lodz.hubertgaw.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public abstract class SportObject {

    private Integer id;

    @NotBlank
    private String name;

    @NotNull
    private Double fullPrice;

    private Set<String> rentEquipmentsNames;

}
