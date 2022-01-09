package pl.lodz.hubertgaw.mapper;

import org.mapstruct.Mapper;
import pl.lodz.hubertgaw.dto.RentEquipment;
import pl.lodz.hubertgaw.repository.*;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportObjectEntity;

import java.util.Locale;

@Mapper(componentModel = "cdi")
public interface RentEquipmentMapper {

    RentEquipmentEntity toEntity(RentEquipment domain);

    RentEquipment toDomain(RentEquipmentEntity entity);

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
        TennisCourtRepository tennisCourtRepository =new TennisCourtRepository();

        String convertedString = value.toLowerCase(Locale.ROOT);
        if (convertedString.contains("tor lekkoatletyczny")) {
            return athleticsTrackRepository.findByName(convertedString).get();
        } else if (convertedString.contains("boisko do siatkówki plażowej")) {
            return beachVolleyballCourtRepository.findByName(convertedString).get();
        } else if (convertedString.contains("ścianka wspinaczkowa")) {
            return climbingWallRepository.findByName(convertedString).get();
        } else if (convertedString.contains("pokój do gry w dart")) {
            return dartRoomRepository.findByName(convertedString).get();
        } else if (convertedString.contains("boisko pełnowymiarowe")) {
            return fullSizePitchRepository.findByName(convertedString).get();
        } else if (convertedString.contains("siłownia")) {
            return gymRepository.findByName(convertedString).get();
        } else if (convertedString.contains("boisko orlik")) {
            return smallPitchRepository.findByName(convertedString).get();
        } else if (convertedString.contains("hala sportowa")) {
            return sportsHallRepository.findByName(convertedString).get();
        } else if (convertedString.contains("basen sportowy")) {
            return sportSwimmingPoolRepository.findByName(convertedString).get();
        } else if (convertedString.contains("kort tenisowy")) {
            return tennisCourtRepository.findByName(convertedString).get();
        } else {
            return beachVolleyballCourtRepository.findByName(convertedString).get();
        }
    }

    default String map(SportObjectEntity value) {
        return value.getName();
    }
}
