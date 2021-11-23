package pl.lodz.hubertgaw.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import pl.lodz.hubertgaw.repository.entity.sports_objects.AthleticsTrackEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportSwimmingPoolEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class SportSwimmingPoolRepository implements PanacheRepositoryBase<SportSwimmingPoolEntity, Integer> {

    public Optional<SportSwimmingPoolEntity> findByName(String name){
        return Optional.of(find("name", name).firstResult());
    }

}
