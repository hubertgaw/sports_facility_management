package pl.lodz.hubertgaw.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import pl.lodz.hubertgaw.repository.entity.sports_objects.AthleticsTrackEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.Optional;

@ApplicationScoped
public class AthleticsTrackRepository implements PanacheRepositoryBase<AthleticsTrackEntity, Integer> {

    public Optional<AthleticsTrackEntity> findByName(String name){
        return Optional.of(find("name", name).firstResult());
    }

}
