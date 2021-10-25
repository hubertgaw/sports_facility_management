package pl.lodz.hubertgaw.mapper;

import org.mapstruct.Mapper;
import pl.lodz.hubertgaw.dto.AthleticsTrack;
import pl.lodz.hubertgaw.repository.entity.sports_objects.AthleticsTrackEntity;

@Mapper(componentModel = "cdi")
public interface AthleticsTrackMapper {

    AthleticsTrackEntity toEntity(AthleticsTrack domain);

    AthleticsTrack toDomain(AthleticsTrackEntity entity);
}
