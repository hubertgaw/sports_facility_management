package pl.lodz.hubertgaw.dto;

import lombok.Data;
import pl.lodz.hubertgaw.repository.entity.RoleEntity;
import pl.lodz.hubertgaw.repository.entity.RoleName;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.*;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    private Integer id;

    @NotBlank(message = "email value cannot be blank")
    @Email(message = "email must be in proper format")
    private String email;

    @NotBlank(message = "first name vannot be blank")
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
