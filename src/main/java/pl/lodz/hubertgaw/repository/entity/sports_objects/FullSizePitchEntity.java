package pl.lodz.hubertgaw.repository.entity.sports_objects;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity(name = "FullSizePitch")
@Table(name = "full_size_pitch")
@Getter
@Setter
public class FullSizePitchEntity extends SportObjectEntity {

    @Column(name = "half_pitch_price")
    @NotNull
    private Double halfPitchPrice;

    @Column(name = "is_full_rented")
    private Boolean isFullRented;
}
