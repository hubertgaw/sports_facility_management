package pl.lodz.hubertgaw.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import pl.lodz.hubertgaw.repository.entity.sports_objects.AthleticsTrackEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.DartRoomEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class DartRoomRepository implements PanacheRepositoryBase<DartRoomEntity, Integer> {

    public Optional<DartRoomEntity> findByName(String name){
        return Optional.of(find("name", name).firstResult());
    }

}
