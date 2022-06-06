package pl.lodz.hubertgaw.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import pl.lodz.hubertgaw.repository.entity.sports_objects.ClimbingWallEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class ClimbingWallRepository implements PanacheRepositoryBase<ClimbingWallEntity, Integer> {

    public ClimbingWallEntity findByName(String name){
        return find("name", name).firstResult();
    }
}
