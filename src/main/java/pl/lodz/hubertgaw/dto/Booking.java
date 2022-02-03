package pl.lodz.hubertgaw.dto;

import lombok.Data;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Booking implements Cloneable {

    private Integer id;

    @NotNull
    private Integer sportObjectId;

    @NotNull
    private LocalDateTime fromDate;

    @NotNull
    @Min(1)
    @Max(12)
    private Integer hours;

    private List<String> rentEquipmentNames = new ArrayList<>();

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    @Email
    private String email;

    @Digits(integer = 9, fraction = 0)
    @Size(min = 9, max = 9)
    private String phoneNumber;

    public void addRentEquipmentName(String rentEquipmentName) {
        this.rentEquipmentNames.add(rentEquipmentName);
    }

    public void removeRentEquipment(String rentEquipmentName) {
        this.rentEquipmentNames.remove(rentEquipmentName);
    }

    @Override
    public Booking clone() {
        try {
            Booking clone = (Booking) super.clone();
            List<String> newRentEquipmentNames = new ArrayList<>(rentEquipmentNames);
            clone.setRentEquipmentNames(newRentEquipmentNames);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
