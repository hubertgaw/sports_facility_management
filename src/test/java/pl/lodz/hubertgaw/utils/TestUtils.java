package pl.lodz.hubertgaw.utils;

import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.microprofile.config.ConfigProvider;
import pl.lodz.hubertgaw.AthleticsTrackResourceTest;
import pl.lodz.hubertgaw.dto.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

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
        fullSizePitch.setIsHalfRentable(Boolean.valueOf(RandomStringUtils.randomNumeric(2)));
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
        smallPitch.setIsHalfRentable(Boolean.valueOf(RandomStringUtils.randomNumeric(2)));
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

    public static User createUser() {
        User user = new User();
        user.setEmail(RandomStringUtils.randomAlphabetic(4) + "@" + RandomStringUtils.randomAlphabetic(4) + "." + RandomStringUtils.randomAlphabetic(3));
        user.setFirstName(RandomStringUtils.randomAlphabetic(10));
        user.setLastName(RandomStringUtils.randomAlphabetic(10));
        user.setPassword(RandomStringUtils.randomAlphabetic(6));
        user.setPhoneNumber(RandomStringUtils.randomNumeric(9));
        return user;
    }

    public static Booking prepareObjectsWithCapacityForBooking(int capacity) {
        AthleticsTrack athleticsTrack = createAthleticsTrack();
        athleticsTrack.setCapacity(capacity);
        RentEquipment rentEquipment1 = createRentEquipment();
        RentEquipment rentEquipment2 = createRentEquipment();

        AthleticsTrack savedTrack = given().port(ConfigProvider.getConfig().getValue("quarkus.http.test-port", Integer.class))
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(athleticsTrack)
                .post("/api/athletics_tracks")
                .then()
                .statusCode(201)
                .extract().as(AthleticsTrack.class);

        RentEquipment savedEquipment1 = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(rentEquipment1)
                .post("/api/rent_equipments")
                .then()
                .statusCode(201)
                .extract().as(RentEquipment.class);

        RentEquipment savedEquipment2 = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(rentEquipment2)
                .post("/api/rent_equipments")
                .then()
                .statusCode(201)
                .extract().as(RentEquipment.class);

        //add equipment to athletics track
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(athleticsTrack)
                .put("/api/athletics_tracks/{athleticsTrackId}/rent_equipment/{rentEquipmentId}",
                        savedTrack.getId(), savedEquipment1.getId());

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(athleticsTrack)
                .put("/api/athletics_tracks/{athleticsTrackId}/rent_equipment/{rentEquipmentId}",
                        savedTrack.getId(), savedEquipment2.getId());



        List<RentEquipment> rentEquipments = Arrays.asList(savedEquipment1, savedEquipment2);

        return createBooking(savedTrack.getId(), rentEquipments);
    }

    public static Booking prepareObjectsWithHalfRentForBooking(boolean halfRent) {
        FullSizePitch fullSizePitch = createFullSizePitch();
        fullSizePitch.setIsHalfRentable(halfRent);
        RentEquipment rentEquipment1 = createRentEquipment();
        RentEquipment rentEquipment2 = createRentEquipment();

        FullSizePitch savedPitch = given().port(ConfigProvider.getConfig().getValue("quarkus.http.test-port", Integer.class))
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(fullSizePitch)
                .post("/api/full_size_pitches")
                .then()
                .statusCode(201)
                .extract().as(FullSizePitch.class);

        RentEquipment savedEquipment1 = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(rentEquipment1)
                .post("/api/rent_equipments")
                .then()
                .statusCode(201)
                .extract().as(RentEquipment.class);

        RentEquipment savedEquipment2 = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(rentEquipment2)
                .post("/api/rent_equipments")
                .then()
                .statusCode(201)
                .extract().as(RentEquipment.class);

        //add equipment to athletics track
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(fullSizePitch)
                .put("/api/full_size_pitches/{athleticsTrackId}/rent_equipment/{rentEquipmentId}",
                        savedPitch.getId(), savedEquipment1.getId());

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(fullSizePitch)
                .put("/api/full_size_pitches/{athleticsTrackId}/rent_equipment/{rentEquipmentId}",
                        savedPitch.getId(), savedEquipment2.getId());



        List<RentEquipment> rentEquipments = Arrays.asList(savedEquipment1, savedEquipment2);

        return createBooking(savedPitch.getId(), rentEquipments);
    }



    public static void clearSportObjectAfterTest(Integer id) {
        given()
                .when()
                .delete("api/sport_objects/{sportObjectId}", id)
                .then()
                .statusCode(204);    }

    public static void clearRentEquipmentAfterTest(Integer id) {
        given()
                .when()
                .delete("api/rent_equipments/{rentEquipmentId}", id)
                .then()
                .statusCode(204);    }

    public static void clearBookingAfterTest(Integer id) {
        given()
                .when()
                .delete("api/bookings/{bookingId}", id)
                .then()
                .statusCode(204);
    }

    public static void clearUserAfterTest(Integer id) {
        given()
                .when()
                .delete("api/users/{userId}", id)
                .then()
                .statusCode(204);
    }
}
