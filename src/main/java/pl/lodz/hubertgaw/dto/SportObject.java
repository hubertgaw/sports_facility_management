package pl.lodz.hubertgaw.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class SportObject implements BookingPossibilities {

    private Integer id;

    @NotBlank(message = "name cannot be blank")
    private String name;

    @NotNull(message = "full price cannot be null")
    private Double fullPrice;

    private Set<String> rentEquipmentNames = new HashSet<>();

    private List<Booking> bookings = new ArrayList<>();

    @Override
    public Integer getCapacity() {
        return null;
    }

    @Override
    public Boolean getIsHalfRentable() {
        return false;
    }
}
