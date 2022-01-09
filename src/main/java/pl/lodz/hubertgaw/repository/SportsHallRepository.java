package pl.lodz.hubertgaw.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportsHallEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class SportsHallRepository implements PanacheRepositoryBase<SportsHallEntity, Integer> {

    public Optional<SportsHallEntity> findByName(String name){
        return Optional.of(find("name", name).firstResult());
    }

}
