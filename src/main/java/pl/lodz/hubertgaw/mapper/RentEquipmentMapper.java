package pl.lodz.hubertgaw.mapper;

import org.mapstruct.Mapper;
import pl.lodz.hubertgaw.dto.RentEquipment;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;

@Mapper(componentModel = "cdi")
public interface RentEquipmentMapper {

    RentEquipmentEntity toEntity(RentEquipment domain);

    RentEquipment toDomain(RentEquipmentEntity entity);
}
