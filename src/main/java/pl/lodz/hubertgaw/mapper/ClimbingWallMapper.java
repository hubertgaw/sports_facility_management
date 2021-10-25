package pl.lodz.hubertgaw.mapper;

import org.mapstruct.Mapper;
import pl.lodz.hubertgaw.dto.ClimbingWall;
import pl.lodz.hubertgaw.repository.entity.sports_objects.ClimbingWallEntity;

@Mapper(componentModel = "cdi")
public interface ClimbingWallMapper {

    ClimbingWallEntity toEntity(ClimbingWall domain);

    ClimbingWall toDomain(ClimbingWallEntity entity);
}
