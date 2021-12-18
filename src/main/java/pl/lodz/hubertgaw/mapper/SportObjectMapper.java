package pl.lodz.hubertgaw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.SubclassMapping;
import pl.lodz.hubertgaw.dto.*;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.*;

import java.util.Locale;

@Mapper(componentModel = "cdi")
public interface SportObjectMapper {

    @SubclassMapping(source = AthleticsTrack.class, target = AthleticsTrackEntity.class)
    @SubclassMapping(source = BeachVolleyballCourt.class, target = BeachVolleyballCourtEntity.class)
    @SubclassMapping(source = ClimbingWall.class, target = ClimbingWallEntity.class)
    @SubclassMapping(source = DartRoom.class, target = DartRoomEntity.class)
    @SubclassMapping(source = FullSizePitch.class, target = FullSizePitchEntity.class)
    @SubclassMapping(source = Gym.class, target = GymEntity.class)
    @SubclassMapping(source = SmallPitch.class, target = SmallPitchEntity.class)
    @SubclassMapping(source = SportsHall.class, target = SportsHallEntity.class)
    @SubclassMapping(source = SportSwimmingPool.class, target = SportSwimmingPoolEntity.class)
    @SubclassMapping(source = TennisCourt.class, target = TennisCourtEntity.class)
    @Mappings({
            @Mapping(source = "rentEquipmentNames", target = "rentEquipment")
    })
    SportObjectEntity map(SportObject domain);

    @SubclassMapping(source = AthleticsTrackEntity.class, target = AthleticsTrack.class)
    @SubclassMapping(source = BeachVolleyballCourtEntity.class, target = BeachVolleyballCourt.class)
    @SubclassMapping(source = ClimbingWallEntity.class, target = ClimbingWall.class)
    @SubclassMapping(source = DartRoomEntity.class, target = DartRoom.class)
    @SubclassMapping(source = FullSizePitchEntity.class, target = FullSizePitch.class)
    @SubclassMapping(source = GymEntity.class, target = Gym.class)
    @SubclassMapping(source = SmallPitchEntity.class, target = SmallPitch.class)
    @SubclassMapping(source = SportsHallEntity.class, target = SportsHall.class)
    @SubclassMapping(source = SportSwimmingPoolEntity.class, target = SportSwimmingPool.class)
    @SubclassMapping(source = TennisCourtEntity.class, target = TennisCourt.class)
    @Mappings({
            @Mapping(source = "rentEquipment", target = "rentEquipmentNames")
    })
    SportObject map(SportObjectEntity entity);

    default RentEquipmentEntity map(String value) {

        RentEquipmentRepository rentEquipmentRepository = new RentEquipmentRepository();
        String convertedString = value.toLowerCase(Locale.ROOT);
        return rentEquipmentRepository.findByName(convertedString).get();

    }

    default String map(RentEquipmentEntity value) {
        return value.getName();
    }


}
