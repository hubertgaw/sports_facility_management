package pl.lodz.hubertgaw.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SmallPitchEntity;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SmallPitchRepository implements PanacheRepositoryBase<SmallPitchEntity, Integer> {
}
