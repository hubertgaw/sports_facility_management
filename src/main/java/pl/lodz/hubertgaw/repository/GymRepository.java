package pl.lodz.hubertgaw.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import pl.lodz.hubertgaw.repository.entity.sports_objects.GymEntity;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GymRepository implements PanacheRepositoryBase<GymEntity, Integer> {

    public GymEntity findByName(String name){
        return find("name", name).firstResult();
    }

}
