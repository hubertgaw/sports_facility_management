package pl.lodz.hubertgaw.repository.entity.sports_objects;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Entity(name = "DartRoom")
@Table(name = "dart_room")
@Getter
@Setter
public class DartRoomEntity extends SportObjectEntity {

    @Column(name = "stands_number")
    @NotEmpty
    private Integer standsNumber;

    @Column(name = "stand_price")
    @NotEmpty
    private Double standPrice;
}
