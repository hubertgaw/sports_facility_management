package pl.lodz.hubertgaw.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RentEquipmentRepository implements PanacheRepositoryBase<RentEquipmentEntity, Integer> {

    public RentEquipmentEntity findByName(String name) {
        return find("name", name).firstResult();
    }

}
