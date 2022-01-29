package pl.lodz.hubertgaw.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import pl.lodz.hubertgaw.repository.entity.sports_objects.FullSizePitchEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class FullSizePitchRepository implements PanacheRepositoryBase<FullSizePitchEntity, Integer> {

    public FullSizePitchEntity findByName(String name){
        return find("name", name).firstResult();
    }

}
