package pl.lodz.hubertgaw.repository.entity.sports_objects;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Entity(name = "SmallPitch")
@Table(name = "small_pitch")
@Getter
@Setter
public class SmallPitchEntity extends SportObjectEntity {

    @Column(name = "half_pitch_price")
    @NotEmpty
    private Double halfPitchPrice;

    @Column(name = "is_full_rented")
    @NotEmpty
    private Boolean isFullRented;
}
