package pl.lodz.hubertgaw.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import pl.lodz.hubertgaw.repository.entity.BookingEntity;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BookingRepository implements PanacheRepositoryBase<BookingEntity, Integer> {

}
