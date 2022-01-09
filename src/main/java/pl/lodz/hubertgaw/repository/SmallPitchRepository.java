package pl.lodz.hubertgaw.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SmallPitchEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class SmallPitchRepository implements PanacheRepositoryBase<SmallPitchEntity, Integer> {

    public Optional<SmallPitchEntity> findByName(String name){
        return Optional.of(find("name", name).firstResult());
    }

}
