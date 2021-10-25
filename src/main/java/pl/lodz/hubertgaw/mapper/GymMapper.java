package pl.lodz.hubertgaw.mapper;

import org.mapstruct.Mapper;
import pl.lodz.hubertgaw.dto.Gym;
import pl.lodz.hubertgaw.repository.entity.sports_objects.GymEntity;

@Mapper(componentModel = "cdi")
public interface GymMapper {

    GymEntity toEntity(Gym domain);

    Gym toDomain(GymEntity entity);
}
