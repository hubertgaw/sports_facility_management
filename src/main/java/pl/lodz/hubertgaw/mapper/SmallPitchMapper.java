package pl.lodz.hubertgaw.mapper;

import org.mapstruct.Mapper;
import pl.lodz.hubertgaw.dto.SmallPitch;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SmallPitchEntity;

@Mapper(componentModel = "cdi")
public interface SmallPitchMapper {

    SmallPitchEntity toEntity(SmallPitch domain);

    SmallPitch toDomain(SmallPitchEntity entity);
}
