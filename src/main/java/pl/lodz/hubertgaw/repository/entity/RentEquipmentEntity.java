package pl.lodz.hubertgaw.repository.entity;

import lombok.Getter;
import lombok.Setter;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportObjectEntity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity(name = "RentEquipment")
@Table(name = "rent_equipment")
@Getter
@Setter
public class RentEquipmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equipment_id")
    private Integer id;

    @Column(name = "name")
    @NotEmpty
    private String name;

    @Column(name = "full_price")
    @NotNull
    private Double price;

    @ManyToMany(mappedBy = "rentEquipment")
    private Set<SportObjectEntity> sportObjects;

    public void addSportObject(SportObjectEntity sportObjectEntity) {
        sportObjects.add(sportObjectEntity);
    }
}
