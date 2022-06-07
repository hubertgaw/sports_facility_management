package pl.lodz.hubertgaw.repository.entity;

import lombok.Getter;
import lombok.Setter;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportObjectEntity;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

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
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "number_of_places")
    private Integer numberOfPlaces;

    @Column(name = "half_rent")
    private Boolean halfRent;

    public void addRentEquipment(RentEquipmentEntity rentEquipmentEntity) {
        this.rentEquipment.add(rentEquipmentEntity);
        rentEquipmentEntity.getBookings().add(this);
    }

    public void removeRentEquipment(RentEquipmentEntity rentEquipmentEntity) {
        this.rentEquipment.remove(rentEquipmentEntity);
//        rentEquipmentEntity.getSportObjects().remove(this);
    }
}
