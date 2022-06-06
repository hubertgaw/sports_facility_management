package pl.lodz.hubertgaw.repository.entity.sports_objects;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "TennisCourt")
@Table(name = "tennis_court")
@Getter
@Setter
public class TennisCourtEntity extends SportObjectEntity {

}
