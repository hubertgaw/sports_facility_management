package pl.lodz.hubertgaw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.lodz.hubertgaw.dto.FullSizePitch;
import pl.lodz.hubertgaw.repository.entity.sports_objects.FullSizePitchEntity;

@Mapper(componentModel = "cdi")
public interface FullSizePitchMapper {

    @Mapping(target = "id", ignore = true)
    FullSizePitchEntity toEntity(FullSizePitch domain);

    FullSizePitch toDomain(FullSizePitchEntity entity);
}
