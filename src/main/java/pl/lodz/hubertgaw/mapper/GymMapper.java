package pl.lodz.hubertgaw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.lodz.hubertgaw.dto.Gym;
import pl.lodz.hubertgaw.repository.entity.sports_objects.GymEntity;

@Mapper(componentModel = "cdi")
public interface GymMapper {

    @Mapping(target = "id", ignore = true)
    GymEntity toEntity(Gym domain);

    Gym toDomain(GymEntity entity);
}
