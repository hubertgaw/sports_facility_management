package pl.lodz.hubertgaw.mapper;

import org.mapstruct.Mapper;
import pl.lodz.hubertgaw.dto.DartRoom;
import pl.lodz.hubertgaw.repository.entity.sports_objects.DartRoomEntity;

@Mapper(componentModel = "cdi")
public interface DartRoomMapper {

    DartRoomEntity toEntity(DartRoom domain);

    DartRoom toDomain(DartRoomEntity entity);
}
