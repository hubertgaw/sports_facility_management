package pl.lodz.hubertgaw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.lodz.hubertgaw.dto.SmallPitch;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SmallPitchEntity;

@Mapper(componentModel = "cdi")
public interface SmallPitchMapper {

    @Mapping(target = "id", ignore = true)
    SmallPitchEntity toEntity(SmallPitch domain);

    SmallPitch toDomain(SmallPitchEntity entity);
}
