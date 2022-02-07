package pl.lodz.hubertgaw.dto;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return id.equals(booking.id) && sportObjectId.equals(booking.sportObjectId) && fromDate.equals(booking.fromDate) && hours.equals(booking.hours) && CollectionUtils.isEqualCollection(rentEquipmentNames, booking.rentEquipmentNames) && Objects.equals(firstName, booking.firstName) && Objects.equals(lastName, booking.lastName) && email.equals(booking.email) && Objects.equals(phoneNumber, booking.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sportObjectId, fromDate, hours, rentEquipmentNames, firstName, lastName, email, phoneNumber);
    }
}
