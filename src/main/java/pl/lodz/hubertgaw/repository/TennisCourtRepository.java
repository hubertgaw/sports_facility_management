package pl.lodz.hubertgaw.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import pl.lodz.hubertgaw.repository.entity.sports_objects.TennisCourtEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class TennisCourtRepository implements PanacheRepositoryBase<TennisCourtEntity, Integer> {

    public TennisCourtEntity findByName(String name){
        return find("name", name).firstResult();
    }

}
