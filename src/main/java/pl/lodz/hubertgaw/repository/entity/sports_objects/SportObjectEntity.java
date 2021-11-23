package pl.lodz.hubertgaw.repository.entity.sports_objects;

import lombok.Getter;
import lombok.Setter;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity(name = "SportObject")
@Table(name = "sport_object")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class SportObjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    @NotEmpty
    private String name;

    @Column(name = "full_price")
    @NotNull
    private Double fullPrice;

    @ManyToMany
    @JoinTable(name = "SPORT_OBJECT_RENT_EQUIPMENT",
            joinColumns = { @JoinColumn(name = "sport_object_id", referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "equipment_id", referencedColumnName = "equipment_id")})
    private Set<RentEquipmentEntity> rentEquipment;

    public void addRentEquipment(RentEquipmentEntity rentEquipmentEntity) {
        rentEquipment.add(rentEquipmentEntity);
    }
}
