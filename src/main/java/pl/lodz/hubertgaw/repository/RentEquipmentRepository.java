package pl.lodz.hubertgaw.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RentEquipmentRepository implements PanacheRepositoryBase<RentEquipmentRepository, Integer> {
}
