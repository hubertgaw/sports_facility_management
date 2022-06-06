package pl.lodz.hubertgaw.repository.entity.sports_objects;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "BeachVolleyballCourt")
@Table(name = "beach_volleyball_court")
@Getter
@Setter
public class BeachVolleyballCourtEntity extends SportObjectEntity {

}
