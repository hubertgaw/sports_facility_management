package pl.lodz.hubertgaw.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import pl.lodz.hubertgaw.repository.entity.sports_objects.CustomObjectEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class CustomObjectRepository implements PanacheRepositoryBase<CustomObjectEntity, Integer> {

    public CustomObjectEntity findByName(String name){
        return find("name", name).firstResult();
    }

    public List<CustomObjectEntity> findByType(String type) {
        return find("type", type).stream().collect(Collectors.toList());
    }

}
