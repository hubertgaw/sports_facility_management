package pl.lodz.hubertgaw.repository.entity.sports_objects;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity(name = "DartRoom")
@Table(name = "dart_room")
@Getter
@Setter
public class DartRoomEntity extends SportObjectEntity {

    @Column(name = "stands_number")
    @NotNull
    private Integer standsNumber;

    @Column(name = "stand_price")
    @NotNull
    private Double standPrice;
}
