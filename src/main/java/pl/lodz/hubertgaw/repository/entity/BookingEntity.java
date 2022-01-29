package pl.lodz.hubertgaw.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportObjectEntity;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Booking")
@Table(name = "booking")
@Getter
@Setter
public class BookingEntity {

    @Column(name = "booking_id")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_object_id")
    private SportObjectEntity sportObject;

    @Column(name = "from_date")
    @NotNull
    private LocalDateTime fromDate;

    @Column(name = "to_date")
    private LocalDateTime toDate;

    @Column(name = "hours")
    @NotNull
    @Min(1)
    @Max(12)
    private Integer hours;

    @ManyToMany
    @JoinTable(name = "booking_rent_equipment",
            joinColumns = { @JoinColumn(name = "booking_id", referencedColumnName = "booking_id") },
            inverseJoinColumns = { @JoinColumn(name = "equipment_id", referencedColumnName = "equipment_id")})
    private List<RentEquipmentEntity> rentEquipment = new ArrayList<>();

    @Column(name = "first_name")
    @NotEmpty
    private String firstName;

    @Column(name = "last_name")
    @NotEmpty
    private String lastName;

    @Column(name = "email")
    @NotEmpty
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    public void addRentEquipment(RentEquipmentEntity rentEquipmentEntity) {
        this.rentEquipment.add(rentEquipmentEntity);
        rentEquipmentEntity.getBookings().add(this);
    }

    public void removeRentEquipment(RentEquipmentEntity rentEquipmentEntity) {
        this.rentEquipment.remove(rentEquipmentEntity);
//        rentEquipmentEntity.getSportObjects().remove(this);
    }
}
