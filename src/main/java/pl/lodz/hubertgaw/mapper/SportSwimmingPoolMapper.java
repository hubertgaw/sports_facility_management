package pl.lodz.hubertgaw.mapper;

import org.mapstruct.Mapper;
import pl.lodz.hubertgaw.dto.SportSwimmingPool;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportSwimmingPoolEntity;

@Mapper(componentModel = "cdi")
public interface SportSwimmingPoolMapper {

    SportSwimmingPoolEntity toEntity(SportSwimmingPool domain);

    SportSwimmingPool toDomain(SportSwimmingPoolEntity entity);

}
