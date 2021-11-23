package pl.lodz.hubertgaw.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import pl.lodz.hubertgaw.repository.entity.sports_objects.AthleticsTrackEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.GymEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class GymRepository implements PanacheRepositoryBase<GymEntity, Integer> {

    public Optional<GymEntity> findByName(String name){
        return Optional.of(find("name", name).firstResult());
    }

}
