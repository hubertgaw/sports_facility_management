package pl.lodz.hubertgaw.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Booking {

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
}
