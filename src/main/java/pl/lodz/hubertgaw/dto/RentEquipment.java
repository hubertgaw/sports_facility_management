package pl.lodz.hubertgaw.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class RentEquipment {

    private Integer id;

    @NotBlank(message = "name cannot be blank")
    private String name;

    @NotNull(message = "price cannot be null")
    private Double price;

    private Set<String> sportObjects = new HashSet<>();

    private List<Integer> bookingsId = new ArrayList<>();
}
