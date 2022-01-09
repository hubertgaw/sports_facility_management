package pl.lodz.hubertgaw.repository.entity.sports_objects;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity(name = "AthleticsTrack")
@Table(name = "athletics_track")
@Getter
@Setter
public class AthleticsTrackEntity extends SportObjectEntity {

    @Column(name = "capacity")
    @NotNull
    private Integer capacity;

    @Column(name = "single_track_price")
    @NotNull
    private Double singleTrackPrice;

}
