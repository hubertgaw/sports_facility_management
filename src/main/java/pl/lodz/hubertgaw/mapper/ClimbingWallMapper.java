package pl.lodz.hubertgaw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.lodz.hubertgaw.dto.ClimbingWall;
import pl.lodz.hubertgaw.repository.entity.sports_objects.ClimbingWallEntity;

@Mapper(componentModel = "cdi")
public interface ClimbingWallMapper {

    @Mapping(target = "id", ignore = true)
    ClimbingWallEntity toEntity(ClimbingWall domain);

    ClimbingWall toDomain(ClimbingWallEntity entity);
}
