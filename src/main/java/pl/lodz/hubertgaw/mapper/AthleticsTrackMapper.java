package pl.lodz.hubertgaw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import pl.lodz.hubertgaw.dto.AthleticsTrack;
import pl.lodz.hubertgaw.repository.entity.sports_objects.AthleticsTrackEntity;

@Mapper(componentModel = "cdi",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AthleticsTrackMapper {

    @Mapping(target = "id", ignore = true)
    AthleticsTrackEntity toEntity(AthleticsTrack domain);

    AthleticsTrack toDomain(AthleticsTrackEntity entity);
}
