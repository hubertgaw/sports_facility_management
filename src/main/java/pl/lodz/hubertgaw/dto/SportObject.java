package pl.lodz.hubertgaw.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Data
public class SportObject {

    private Integer id;

    @NotBlank
    private String name;

    @NotNull
    private Double fullPrice;

    private Set<String> rentEquipmentNames = new HashSet<>();

}
