package pl.lodz.hubertgaw.mapper;

import org.mapstruct.Mapper;
import pl.lodz.hubertgaw.dto.TennisCourt;
import pl.lodz.hubertgaw.repository.entity.sports_objects.TennisCourtEntity;

@Mapper(componentModel = "cdi")
public interface TennisCourtMapper {

    TennisCourtEntity toEntity(TennisCourt domain);

    TennisCourt toDomain(TennisCourtEntity entity);
}
