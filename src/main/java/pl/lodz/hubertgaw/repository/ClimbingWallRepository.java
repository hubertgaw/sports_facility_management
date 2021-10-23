package pl.lodz.hubertgaw.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import pl.lodz.hubertgaw.repository.entity.sports_objects.ClimbingWallEntity;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ClimbingWallRepository implements PanacheRepositoryBase<ClimbingWallEntity, Integer> {
}
