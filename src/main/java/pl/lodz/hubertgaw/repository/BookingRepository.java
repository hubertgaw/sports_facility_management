package pl.lodz.hubertgaw.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import pl.lodz.hubertgaw.repository.entity.BookingEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class BookingRepository implements PanacheRepositoryBase<BookingEntity, Integer> {

    public List<BookingEntity> findByUserId(Integer userId) {
        return list("user_id", userId);
    }

}
