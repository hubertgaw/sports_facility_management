package pl.lodz.hubertgaw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;
import pl.lodz.hubertgaw.dto.*;
import pl.lodz.hubertgaw.repository.entity.sports_objects.*;

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
    SportObject map(SportObjectEntity entity);
}
