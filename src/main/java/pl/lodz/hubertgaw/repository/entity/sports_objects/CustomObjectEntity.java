package pl.lodz.hubertgaw.repository.entity.sports_objects;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity(name = "CustomObject")
@Table(name = "custom_object")
@Getter
@Setter
public class CustomObjectEntity extends SportObjectEntity {

    @Column(name = "type")
    @NotNull
    private String type;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "single_price")
    private Double singlePrice;

    @Column(name = "is_half_rentable")
    private Boolean isHalfRentable;

}
