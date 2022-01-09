package pl.lodz.hubertgaw.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportObjectEntity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@JsonIgnoreProperties({"hibernateLazyInitializer"})
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

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "rentEquipment")
    private Set<SportObjectEntity> sportObjects = new HashSet<>();

    public void addSportObject(SportObjectEntity sportObjectEntity) {
        this.sportObjects.add(sportObjectEntity);
        sportObjectEntity.getRentEquipment().add(this);
    }

    public void removeSportObject(SportObjectEntity sportObjectEntity) {
        this.sportObjects.remove(sportObjectEntity);
//        sportObjectEntity.getRentEquipment().remove(this);
    }
}
