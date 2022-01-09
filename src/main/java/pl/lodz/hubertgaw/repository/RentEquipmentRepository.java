package pl.lodz.hubertgaw.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class RentEquipmentRepository implements PanacheRepositoryBase<RentEquipmentEntity, Integer> {

    public Optional<RentEquipmentEntity> findByName(String name){
        return Optional.of(find("name", name).firstResult());
    }

}
