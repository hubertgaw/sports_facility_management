package pl.lodz.hubertgaw.repository.entity.sports_objects;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity(name = "ClimbingWall")
@Table(name = "climbing_wall")
@Getter
@Setter
public class ClimbingWallEntity extends SportObjectEntity {

    @Column(name = "capacity")
    @NotNull
    private Integer capacity;

    @Column(name = "single_price")
    @NotNull
    private Double singlePrice;

}
