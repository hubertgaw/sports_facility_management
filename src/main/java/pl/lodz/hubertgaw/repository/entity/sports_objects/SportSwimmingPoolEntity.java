package pl.lodz.hubertgaw.repository.entity.sports_objects;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity(name = "SportSwimmingPool")
@Table(name = "sport_swimming_pool")
@Getter
@Setter
public class SportSwimmingPoolEntity extends SportObjectEntity {

    @Column(name = "tracks_number")
    @NotNull
    private Integer tracksNumber;

    @Column(name = "track_price")
    @NotNull
    private Double trackPrice;
}
