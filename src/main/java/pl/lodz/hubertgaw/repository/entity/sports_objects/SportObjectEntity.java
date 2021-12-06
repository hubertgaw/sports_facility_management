package pl.lodz.hubertgaw.repository.entity.sports_objects;

import lombok.Getter;
import lombok.Setter;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "SportObject")
@Table(name = "sport_object")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class SportObjectEntity {

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

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "sport_object_rent_equipment",
            joinColumns = { @JoinColumn(name = "sport_object_id", referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "equipment_id", referencedColumnName = "equipment_id")})
    private Set<RentEquipmentEntity> rentEquipment = new HashSet<>();

    public void addRentEquipment(RentEquipmentEntity rentEquipmentEntity) {
        rentEquipmentEntity.addSportObject(this);
        rentEquipment.add(rentEquipmentEntity);
    }
}
