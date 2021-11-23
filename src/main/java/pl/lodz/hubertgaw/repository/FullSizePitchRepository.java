package pl.lodz.hubertgaw.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import pl.lodz.hubertgaw.repository.entity.sports_objects.AthleticsTrackEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.FullSizePitchEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class FullSizePitchRepository implements PanacheRepositoryBase<FullSizePitchEntity, Integer> {

    public Optional<FullSizePitchEntity> findByName(String name){
        return Optional.of(find("name", name).firstResult());
    }

}
