package pl.lodz.hubertgaw.mapper;

import org.mapstruct.Mapper;
import pl.lodz.hubertgaw.dto.FullSizePitch;
import pl.lodz.hubertgaw.repository.entity.sports_objects.FullSizePitchEntity;

@Mapper(componentModel = "cdi")
public interface FullSizePitchMapper {

    FullSizePitchEntity toEntity(FullSizePitch domain);

    FullSizePitch toDomain(FullSizePitchEntity entity);
}
