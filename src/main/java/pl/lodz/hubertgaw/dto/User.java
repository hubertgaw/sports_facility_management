package pl.lodz.hubertgaw.dto;

import lombok.Data;
import pl.lodz.hubertgaw.repository.entity.RoleName;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    private Integer id;

    @NotBlank(message = "email value cannot be blank")
    @Email(message = "email must be in proper format")
    private String email;

    @NotBlank(message = "first name cannot be blank")
    private String firstName;

    @NotBlank(message = "last name cannot be blank")
    private String lastName;

    @NotBlank(message = "password cannot be blank")
    private String password;

    @Digits(integer = 9, fraction = 0, message = "phone number can contain only digits")
    @Size(min = 9, max = 9, message = "phone number must be 9 digits long")
    private String phoneNumber;

    private Set<RoleName> roles = new HashSet<>();

    private Set<Booking> bookings = new HashSet<>();
}
