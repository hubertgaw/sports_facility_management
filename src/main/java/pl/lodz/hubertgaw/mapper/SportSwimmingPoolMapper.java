package pl.lodz.hubertgaw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.lodz.hubertgaw.dto.SportSwimmingPool;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportSwimmingPoolEntity;

@Mapper(componentModel = "cdi")
public interface SportSwimmingPoolMapper {

    @Mapping(target = "id", ignore = true)
    SportSwimmingPoolEntity toEntity(SportSwimmingPool domain);

    SportSwimmingPool toDomain(SportSwimmingPoolEntity entity);

}
