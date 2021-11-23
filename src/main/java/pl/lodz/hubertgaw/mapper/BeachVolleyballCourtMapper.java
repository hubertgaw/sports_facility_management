package pl.lodz.hubertgaw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.lodz.hubertgaw.dto.BeachVolleyballCourt;
import pl.lodz.hubertgaw.repository.entity.sports_objects.BeachVolleyballCourtEntity;

@Mapper(componentModel = "cdi")
public interface BeachVolleyballCourtMapper {

    @Mapping(target = "id", ignore = true)
    BeachVolleyballCourtEntity toEntity(BeachVolleyballCourt domain);

    BeachVolleyballCourt toDomain(BeachVolleyballCourtEntity entity);
}
