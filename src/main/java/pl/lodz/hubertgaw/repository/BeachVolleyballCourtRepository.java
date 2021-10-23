package pl.lodz.hubertgaw.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import pl.lodz.hubertgaw.repository.entity.sports_objects.BeachVolleyballCourtEntity;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BeachVolleyballCourtRepository implements PanacheRepositoryBase<BeachVolleyballCourtEntity,Integer> {
}
