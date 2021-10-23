package pl.lodz.hubertgaw.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportSwimmingPoolEntity;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SportSwimmingPoolRepository implements PanacheRepositoryBase<SportSwimmingPoolEntity, Integer> {
}
