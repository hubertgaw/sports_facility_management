package pl.lodz.hubertgaw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import pl.lodz.hubertgaw.dto.Booking;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.SportObjectRepository;
import pl.lodz.hubertgaw.repository.entity.BookingEntity;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportObjectEntity;

import java.util.Locale;

@Mapper(componentModel = "cdi")
public interface BookingMapper {

    @Mappings({
            @Mapping(source = "sportObjectId", target = "sportObject"),
            @Mapping(source = "rentEquipmentNames", target = "rentEquipment"),
            @Mapping(target = "toDate", expression = "java(domain.getFromDate().plusHours(domain.getHours()))")
    })
    BookingEntity toEntity(Booking domain);

    @Mappings({
            @Mapping(source = "sportObject", target = "sportObjectId"),
            @Mapping(source = "rentEquipment", target = "rentEquipmentNames")
    })
    Booking toDomain(BookingEntity entity);

    default SportObjectEntity map(Integer id) {

        SportObjectRepository sportObjectRepository = new SportObjectRepository();

        return sportObjectRepository.findById(id);
    }

    default Integer map(SportObjectEntity entity) {
        return entity.getId();
    }

    default RentEquipmentEntity map(String value) {

        RentEquipmentRepository rentEquipmentRepository = new RentEquipmentRepository();
        String convertedString = value.toLowerCase(Locale.ROOT);
        return rentEquipmentRepository.findByName(convertedString);

    }

    default String map(RentEquipmentEntity value) {
        return value.getName();
    }
}
