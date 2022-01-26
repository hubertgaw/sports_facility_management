package pl.lodz.hubertgaw.mapper;

import org.mapstruct.Mapper;
import pl.lodz.hubertgaw.dto.Booking;
import pl.lodz.hubertgaw.repository.entity.BookingEntity;

@Mapper(componentModel = "cdi")
public interface BookingMapper {

    BookingEntity toEntity(Booking domain);

    Booking toDomain(BookingEntity entity);
}
