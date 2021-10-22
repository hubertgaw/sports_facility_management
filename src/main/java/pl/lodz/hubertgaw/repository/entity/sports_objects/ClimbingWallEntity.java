package pl.lodz.hubertgaw.repository.entity.sports_objects;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Entity(name = "ClimbingWall")
@Table(name = "climbing_wall")
@Getter
@Setter
public class ClimbingWallEntity extends SportObjectEntity {

    @Column(name = "capacity")
    @NotEmpty
    private Integer capacity;

    @Column(name = "single_price")
    @NotEmpty
    private Double singlePrice;

}
