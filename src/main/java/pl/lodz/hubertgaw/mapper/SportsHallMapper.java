package pl.lodz.hubertgaw.mapper;

import org.mapstruct.Mapper;
import pl.lodz.hubertgaw.dto.SportsHall;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportsHallEntity;

@Mapper(componentModel = "cdi")
public interface SportsHallMapper {

    SportsHallEntity toEntity(SportsHall domain);

    SportsHall toDomain(SportsHallEntity entity);
}
