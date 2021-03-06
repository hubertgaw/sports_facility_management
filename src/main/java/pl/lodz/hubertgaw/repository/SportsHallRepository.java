package pl.lodz.hubertgaw.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportsHallEntity;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SportsHallRepository implements PanacheRepositoryBase<SportsHallEntity, Integer> {

    public SportsHallEntity findByName(String name){
        return find("name", name).firstResult();
    }

}
