package pl.lodz.hubertgaw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.lodz.hubertgaw.dto.SportsHall;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportsHallEntity;

@Mapper(componentModel = "cdi")
public interface SportsHallMapper {

    @Mapping(target = "id", ignore = true)
    SportsHallEntity toEntity(SportsHall domain);

    SportsHall toDomain(SportsHallEntity entity);
}
