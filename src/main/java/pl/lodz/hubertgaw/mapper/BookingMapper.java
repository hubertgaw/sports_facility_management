package pl.lodz.hubertgaw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import pl.lodz.hubertgaw.dto.Booking;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.SportObjectRepository;
import pl.lodz.hubertgaw.repository.UserRepository;
import pl.lodz.hubertgaw.repository.entity.BookingEntity;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.UserEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportObjectEntity;

import java.util.Locale;

@Mapper(componentModel = "cdi")
public interface BookingMapper {

    @Mappings({
            @Mapping(source = "sportObjectId", target = "sportObject"),
            @Mapping(source = "rentEquipmentNames", target = "rentEquipment"),
            @Mapping(source = "userId", target = "user"),
            @Mapping(target = "toDate", expression = "java(domain.getFromDate().plusHours(domain.getHours()))")
    })
    BookingEntity toEntity(Booking domain);

    @Mappings({
            @Mapping(source = "sportObject", target = "sportObjectId"),
            @Mapping(source = "rentEquipment", target = "rentEquipmentNames"),
            @Mapping(source = "user", target = "userId")
    })
    Booking toDomain(BookingEntity entity);

    default SportObjectEntity mapSportObject(Integer objectId) {

        SportObjectRepository sportObjectRepository = new SportObjectRepository();

        return sportObjectRepository.findById(objectId);
    }

    default Integer mapSportObject(SportObjectEntity entity) {
        return entity.getId();
    }

    default UserEntity mapUser(Integer userId) {
        UserRepository userRepository = new UserRepository();

        if (0 == userId) {
            return null;
        }
        return userRepository.findById(userId);
    }

    default Integer mapUser(UserEntity entity) {
        if (null == entity) {
            return 0;
        }
        return entity.getId();
    }


    default RentEquipmentEntity mapRentEquipment(String value) {

        RentEquipmentRepository rentEquipmentRepository = new RentEquipmentRepository();
        String convertedString = value.toLowerCase(Locale.ROOT);
        return rentEquipmentRepository.findByName(convertedString);

    }

    default String mapRentEquipment(RentEquipmentEntity value) {
        return value.getName();
    }
}
