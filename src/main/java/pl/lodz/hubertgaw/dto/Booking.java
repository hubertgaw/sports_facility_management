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

    @NotNull(message = "sport object id cannot be null")
    private Integer sportObjectId;

    private Integer userId;

    @NotNull(message = "from date cannot be null")
    private LocalDateTime fromDate;

    @NotNull(message = "hours cannot be null")
    @Min(value = 1, message = "hours value cannot be less than 1")
    @Max(value = 12, message = "hours value cannot be more than 12")
    private Integer hours;

    private List<String> rentEquipmentNames = new ArrayList<>();

    private String firstName;

    private String lastName;

    @Email(message = "email must be in proper format")
    private String email;

    @Digits(integer = 9, fraction = 0, message = "phone number can contain only digits")
    @Size(min = 9, max = 9, message = "phone number must be 9 digits long")
    private String phoneNumber;

    @Min(value = 0, message = "number of places cannot be less than 0")
    @Max(value = 50, message = "number of places cannot be more than 50")
    private Integer numberOfPlaces;

    private Boolean halfRent;

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
        return id.equals(booking.id) && sportObjectId.equals(booking.sportObjectId) && fromDate.equals(booking.fromDate) && hours.equals(booking.hours) && CollectionUtils.isEqualCollection(rentEquipmentNames, booking.rentEquipmentNames) && Objects.equals(firstName, booking.firstName) && Objects.equals(lastName, booking.lastName) && email == booking.email && Objects.equals(phoneNumber, booking.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sportObjectId, fromDate, hours, rentEquipmentNames, firstName, lastName, email, phoneNumber);
    }
}
