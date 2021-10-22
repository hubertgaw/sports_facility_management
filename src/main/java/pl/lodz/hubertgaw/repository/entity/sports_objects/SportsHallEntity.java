package pl.lodz.hubertgaw.repository.entity.sports_objects;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Entity(name = "SportsHall")
@Table(name = "sports_hall")
@Getter
@Setter
public class SportsHallEntity extends SportObjectEntity {

    @Column(name = "sectors_number")
    @NotEmpty
    private Integer sectorsNumber;

    @Column(name = "sector_price")
    @NotEmpty
    private Double sectorPrice;
}
