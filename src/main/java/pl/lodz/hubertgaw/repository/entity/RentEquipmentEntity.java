package pl.lodz.hubertgaw.repository.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity(name = "RentEquipment")
@Table(name = "rent_equipment")
@Getter
@Setter
@Embeddable
public class RentEquipmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    @NotEmpty
    private String name;

    @Column(name = "full_price")
    @NotEmpty
    private Double price;
}
