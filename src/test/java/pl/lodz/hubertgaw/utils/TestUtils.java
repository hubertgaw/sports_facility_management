package pl.lodz.hubertgaw.utils;

import org.apache.commons.lang3.RandomStringUtils;
import pl.lodz.hubertgaw.dto.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestUtils {
    public static String getActualExceptionMessage(String response) {
        String[] parts = response.split(" ", 2);
        return parts[1];
    }

    public static RentEquipment createRentEquipment() {
        RentEquipment rentEquipment = new RentEquipment();
        rentEquipment.setName(RandomStringUtils.randomAlphabetic(10));
        rentEquipment.setPrice(Double.valueOf(RandomStringUtils.randomNumeric(2)));
        return rentEquipment;
    }

    public static AthleticsTrack createAthleticsTrack() {
        AthleticsTrack athleticsTrack = new AthleticsTrack();
        athleticsTrack.setName(RandomStringUtils.randomAlphabetic(10));
        athleticsTrack.setFullPrice(Double.valueOf(RandomStringUtils.randomNumeric(2)));
        athleticsTrack.setCapacity(Integer.valueOf(RandomStringUtils.randomNumeric(2)));
        athleticsTrack.setSingleTrackPrice(Double.valueOf(RandomStringUtils.randomNumeric(1)));
        return athleticsTrack;
    }

    public static BeachVolleyballCourt createBeachVolleyballCourt() {
        BeachVolleyballCourt beachVolleyballCourt = new BeachVolleyballCourt();
        beachVolleyballCourt.setName(RandomStringUtils.randomAlphabetic(10));
        beachVolleyballCourt.setFullPrice(Double.valueOf(RandomStringUtils.randomNumeric(2)));
        return beachVolleyballCourt;
    }

    public static ClimbingWall createClimbingWall() {
        ClimbingWall climbingWall = new ClimbingWall();
        climbingWall.setName(RandomStringUtils.randomAlphabetic(10));
        climbingWall.setFullPrice(Double.valueOf(RandomStringUtils.randomNumeric(2)));
        climbingWall.setCapacity(Integer.valueOf(RandomStringUtils.randomNumeric(2)));
        climbingWall.setSinglePrice(Double.valueOf(RandomStringUtils.randomNumeric(1)));
        return climbingWall;
    }

    public static DartRoom createDartRoom() {
        DartRoom dartRoom = new DartRoom();
        dartRoom.setName(RandomStringUtils.randomAlphabetic(10));
        dartRoom.setFullPrice(Double.valueOf(RandomStringUtils.randomNumeric(2)));
        dartRoom.setStandsNumber(Integer.valueOf(RandomStringUtils.randomNumeric(2)));
        dartRoom.setStandPrice(Double.valueOf(RandomStringUtils.randomNumeric(1)));
        return dartRoom;
    }

    public static FullSizePitch createFullSizePitch() {
        FullSizePitch fullSizePitch = new FullSizePitch();
        fullSizePitch.setName(RandomStringUtils.randomAlphabetic(10));
        fullSizePitch.setFullPrice(Double.valueOf(RandomStringUtils.randomNumeric(2)));
        fullSizePitch.setIsFullRented(Boolean.valueOf(RandomStringUtils.randomNumeric(2)));
        fullSizePitch.setHalfPitchPrice(Double.valueOf(RandomStringUtils.randomNumeric(1)));
        return fullSizePitch;
    }

    public static Gym createGym() {
        Gym gym = new Gym();
        gym.setName(RandomStringUtils.randomAlphabetic(10));
        gym.setFullPrice(Double.valueOf(RandomStringUtils.randomNumeric(2)));
        gym.setCapacity(Integer.valueOf(RandomStringUtils.randomNumeric(2)));
        gym.setSinglePrice(Double.valueOf(RandomStringUtils.randomNumeric(1)));
        return gym;
    }

    public static SmallPitch createSmallPitch() {
        SmallPitch smallPitch = new SmallPitch();
        smallPitch.setName(RandomStringUtils.randomAlphabetic(10));
        smallPitch.setFullPrice(Double.valueOf(RandomStringUtils.randomNumeric(2)));
        smallPitch.setIsFullRented(Boolean.valueOf(RandomStringUtils.randomNumeric(2)));
        smallPitch.setHalfPitchPrice(Double.valueOf(RandomStringUtils.randomNumeric(1)));
        return smallPitch;
    }

    public static SportsHall createSportsHall() {
        SportsHall sportsHall = new SportsHall();
        sportsHall.setName(RandomStringUtils.randomAlphabetic(10));
        sportsHall.setFullPrice(Double.valueOf(RandomStringUtils.randomNumeric(2)));
        sportsHall.setSectorsNumber(Integer.valueOf(RandomStringUtils.randomNumeric(2)));
        sportsHall.setSectorPrice(Double.valueOf(RandomStringUtils.randomNumeric(1)));
        return sportsHall;
    }


    public static SportSwimmingPool createSportSwimmingPool() {
        SportSwimmingPool sportSwimmingPool = new SportSwimmingPool();
        sportSwimmingPool.setName(RandomStringUtils.randomAlphabetic(10));
        sportSwimmingPool.setFullPrice(Double.valueOf(RandomStringUtils.randomNumeric(2)));
        sportSwimmingPool.setTracksNumber(Integer.valueOf(RandomStringUtils.randomNumeric(2)));
        sportSwimmingPool.setTrackPrice(Double.valueOf(RandomStringUtils.randomNumeric(1)));
        return sportSwimmingPool;
    }

    public static TennisCourt createTennisCourt() {
        TennisCourt tennisCourt = new TennisCourt();
        tennisCourt.setName(RandomStringUtils.randomAlphabetic(10));
        tennisCourt.setFullPrice(Double.valueOf(RandomStringUtils.randomNumeric(2)));
        return tennisCourt;
    }

    public static CustomObject createCustomObject() {
        CustomObject customObject = new CustomObject();
        customObject.setType(RandomStringUtils.randomAlphabetic(5));
        customObject.setName(RandomStringUtils.randomAlphabetic(10));
        customObject.setFullPrice(Double.valueOf(RandomStringUtils.randomNumeric(2)));
        customObject.setCapacity(Integer.valueOf(RandomStringUtils.randomNumeric(2)));
        return customObject;
    }

    public static Booking createBooking(Integer sportObjectId, List<RentEquipment> rentEquipments) {
        Booking booking = new Booking();
        booking.setSportObjectId(sportObjectId);
        int nowMinutes = LocalDateTime.now().getMinute();
        int minutesToAdd = 30 - (nowMinutes % 30); // we need time in format with 30 or 00 minutes
        booking.setFromDate(LocalDateTime.now().plusMinutes(minutesToAdd));
        booking.setHours(3);
        booking.setRentEquipmentNames(rentEquipments.stream().map(RentEquipment::getName).collect(Collectors.toList()));
        booking.setFirstName("Andrzej");
        booking.setLastName("Kowalski");
        booking.setEmail("test@mail.com");
        booking.setPhoneNumber("123456789");

        return booking;
    }

}
