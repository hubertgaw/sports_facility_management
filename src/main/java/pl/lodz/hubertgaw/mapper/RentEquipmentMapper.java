package pl.lodz.hubertgaw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import pl.lodz.hubertgaw.dto.RentEquipment;
import pl.lodz.hubertgaw.repository.*;
import pl.lodz.hubertgaw.repository.entity.BookingEntity;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportObjectEntity;

import java.util.Locale;

@Mapper(componentModel = "cdi")
public interface RentEquipmentMapper {

    @Mappings({
            @Mapping(target = "sportObjects", ignore = true),
            @Mapping(target = "bookings", ignore = true)
    })
    RentEquipmentEntity toEntity(RentEquipment domain);

    @Mappings({
            @Mapping(source = "bookings", target = "bookingsId")
    })
    RentEquipment toDomain(RentEquipmentEntity entity);

    //na ten moment nie używana metoda, bo nie jest umożliwione dodawanie obiektu od razu przy tworzenia rentEquipment
    default SportObjectEntity map(String value) {

        AthleticsTrackRepository athleticsTrackRepository = new AthleticsTrackRepository();
        BeachVolleyballCourtRepository beachVolleyballCourtRepository = new BeachVolleyballCourtRepository();
        ClimbingWallRepository climbingWallRepository = new ClimbingWallRepository();
        DartRoomRepository dartRoomRepository = new DartRoomRepository();
        FullSizePitchRepository fullSizePitchRepository = new FullSizePitchRepository();
        GymRepository gymRepository = new GymRepository();
        SmallPitchRepository smallPitchRepository = new SmallPitchRepository();
        SportsHallRepository sportsHallRepository = new SportsHallRepository();
        SportSwimmingPoolRepository sportSwimmingPoolRepository = new SportSwimmingPoolRepository();
        TennisCourtRepository tennisCourtRepository = new TennisCourtRepository();
        CustomObjectRepository customObjectRepository = new CustomObjectRepository();

        String convertedString = value.toLowerCase(Locale.ROOT);
        if (convertedString.contains("tor lekkoatletyczny")) {
            return athleticsTrackRepository.findByName(convertedString);
        } else if (convertedString.contains("boisko do siatkówki plażowej")) {
            return beachVolleyballCourtRepository.findByName(convertedString);
        } else if (convertedString.contains("ścianka wspinaczkowa")) {
            return climbingWallRepository.findByName(convertedString);
        } else if (convertedString.contains("pokój do gry w dart")) {
            return dartRoomRepository.findByName(convertedString);
        } else if (convertedString.contains("boisko pełnowymiarowe")) {
            return fullSizePitchRepository.findByName(convertedString);
        } else if (convertedString.contains("siłownia")) {
            return gymRepository.findByName(convertedString);
        } else if (convertedString.contains("boisko orlik")) {
            return smallPitchRepository.findByName(convertedString);
        } else if (convertedString.contains("hala sportowa")) {
            return sportsHallRepository.findByName(convertedString);
        } else if (convertedString.contains("basen sportowy")) {
            return sportSwimmingPoolRepository.findByName(convertedString);
        } else if (convertedString.contains("kort tenisowy")) {
            return tennisCourtRepository.findByName(convertedString);
        } else {
            return customObjectRepository.findByName(convertedString);
        }
    }

    default String map(SportObjectEntity value) {
        return value.getName();
    }

    default Integer map(BookingEntity value) {
        return value.getId();
    }
}
