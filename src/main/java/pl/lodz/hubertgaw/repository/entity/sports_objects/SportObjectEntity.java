package pl.lodz.hubertgaw.repository.entity.sports_objects;

import lombok.Getter;
import lombok.Setter;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

@Entity(name = "SportObject")
@Table(name = "sport_object")
@Getter
@Setter
public class SportObjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    @NotEmpty
    private String name;

    @Column(name = "full_price")
    @NotEmpty
    private Double fullPrice;

    @ManyToMany
    @JoinTable(name = "SPORT_OBJECT_RENT_EQUIPMENT",
            joinColumns = @JoinColumn(name = "sport_object_id"),
            inverseJoinColumns = @JoinColumn(name = "equipment_id"))
    private Set<RentEquipmentEntity> rentEquipment;

}
