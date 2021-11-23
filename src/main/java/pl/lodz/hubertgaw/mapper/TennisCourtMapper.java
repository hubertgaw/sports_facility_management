package pl.lodz.hubertgaw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.lodz.hubertgaw.dto.TennisCourt;
import pl.lodz.hubertgaw.repository.entity.sports_objects.TennisCourtEntity;

@Mapper(componentModel = "cdi")
public interface TennisCourtMapper {

    @Mapping(target = "id", ignore = true)
    TennisCourtEntity toEntity(TennisCourt domain);

    TennisCourt toDomain(TennisCourtEntity entity);
}
