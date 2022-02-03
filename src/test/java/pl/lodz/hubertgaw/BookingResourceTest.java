package pl.lodz.hubertgaw;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import pl.lodz.hubertgaw.dto.AthleticsTrack;
import pl.lodz.hubertgaw.dto.Booking;
import pl.lodz.hubertgaw.dto.ClimbingWall;
import pl.lodz.hubertgaw.dto.RentEquipment;
import pl.lodz.hubertgaw.mapper.BookingMapper;
import pl.lodz.hubertgaw.mapper.RentEquipmentMapper;
import pl.lodz.hubertgaw.repository.entity.BookingEntity;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.service.BookingService;
import pl.lodz.hubertgaw.service.RentEquipmentService;
import pl.lodz.hubertgaw.service.SportObjectService;
import pl.lodz.hubertgaw.utils.TestUtils;

import javax.inject.Inject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static pl.lodz.hubertgaw.utils.TestUtils.createAthleticsTrack;
import static pl.lodz.hubertgaw.utils.TestUtils.createBooking;

@QuarkusTest
public class BookingResourceTest {


    @Inject
    BookingService bookingService;
    @Inject
    SportObjectService sportObjectService;
    @Inject
    RentEquipmentService rentEquipmentService;
    @Inject
    RentEquipmentMapper rentEquipmentMapper;
    @Inject
    BookingMapper bookingMapper;


    @Test
    public void getAll() {
        given()
                .when().get("/api/bookings")
                .then()
                .statusCode(200);
    }

    @Test
    public void getById() {
        Booking booking = prepareObjectsForBooking();

        Booking savedBooking = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(booking)
                .post("/api/bookings")
                .then()
                .statusCode(201)
                .extract().as(Booking.class);
        Booking gotBooking = given()
                .when().get("/api/bookings/{bookingId}", savedBooking.getId())
                .then()
                .statusCode(200)
                .extract().as(Booking.class);

        assertThat(savedBooking).isEqualTo(gotBooking);
        clearBookingAfterTest(savedBooking.getId());
        BookingEntity bookingEntity = bookingMapper.toEntity(savedBooking);
        for (RentEquipmentEntity rentEquipmentEntity : bookingEntity.getRentEquipment()) {
            clearRentEquipmentAfterTest(rentEquipmentEntity.getId());
        }
        clearAthleticsTrackAfterTest(bookingEntity.getSportObject().getId());
    }

    @Test
    public void post() {
        Booking booking = prepareObjectsForBooking();

        Booking saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(booking)
                .post("/api/bookings")
                .then()
                .statusCode(201)
                .extract().as(Booking.class);
        assertThat(saved.getId()).isNotNull();
        clearBookingAfterTest(saved.getId());
        BookingEntity bookingEntity = bookingMapper.toEntity(saved);
        for (RentEquipmentEntity rentEquipmentEntity : bookingEntity.getRentEquipment()) {
            clearRentEquipmentAfterTest(rentEquipmentEntity.getId());
        }
        clearAthleticsTrackAfterTest(bookingEntity.getSportObject().getId());

    }

    @Test
    public void put() {

        Booking booking = prepareObjectsForBooking();
        Booking saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(booking)
                .post("/api/bookings")
                .then()
                .statusCode(201)
                .extract().as(Booking.class);
        saved.setLastName("Updated");
        Booking updated = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/bookings")
                .then()
                .statusCode(200)
                .extract().as(Booking.class);
        assertThat(updated.getLastName()).isEqualTo("Updated");
        clearBookingAfterTest(saved.getId());
        BookingEntity bookingEntity = bookingMapper.toEntity(saved);
        for (RentEquipmentEntity rentEquipmentEntity : bookingEntity.getRentEquipment()) {
            clearRentEquipmentAfterTest(rentEquipmentEntity.getId());
        }
        clearAthleticsTrackAfterTest(bookingEntity.getSportObject().getId());


    }


    @Test
    public void putFailNotFound() {
        Booking booking = prepareObjectsForBooking();

        //there will never be id 0 in repo.
        booking.setId(0);
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(booking)
                .put("/api/bookings")
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Booking for given id not found!");

        for (String rentEquipmentName : booking.getRentEquipmentNames()) {
            clearRentEquipmentAfterTest(rentEquipmentService.findByName(rentEquipmentName).getId());
        }
        clearAthleticsTrackAfterTest(booking.getSportObjectId());

    }

    @Test
    public void putFailEmptyId() {
        Booking booking = prepareObjectsForBooking();
        Booking saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(booking)
                .post("/api/bookings")
                .then()
                .statusCode(201)
                .extract().as(Booking.class);
        int id = saved.getId(); // we will need that to delete after test.
        saved.setId(null);
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/bookings")
                .then()
                .statusCode(400)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(actualExceptionMessage).isEqualTo("Id for updating booking cannot be null");

        clearBookingAfterTest(id);
        BookingEntity bookingEntity = bookingMapper.toEntity(saved);
        for (RentEquipmentEntity rentEquipmentEntity : bookingEntity.getRentEquipment()) {
            clearRentEquipmentAfterTest(rentEquipmentEntity.getId());
        }
        clearAthleticsTrackAfterTest(bookingEntity.getSportObject().getId());
    }

    @Test
    public void delete() {
        Booking booking = prepareObjectsForBooking();
        Booking saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(booking)
                .post("/api/bookings")
                .then()
                .statusCode(201)
                .extract().as(Booking.class);
        given()
                .when()
                .delete("api/bookings/{bookingId}", saved.getId())
                .then()
                .statusCode(204);
        //check if booking with that id is not found:
        given()
                .when().get("/api/bookings/{bookingId}", saved.getId())
                .then()
                .statusCode(404);

        BookingEntity bookingEntity = bookingMapper.toEntity(saved);
        for (RentEquipmentEntity rentEquipmentEntity : bookingEntity.getRentEquipment()) {
            clearRentEquipmentAfterTest(rentEquipmentEntity.getId());
        }
        clearAthleticsTrackAfterTest(bookingEntity.getSportObject().getId());
    }

    @Test
    public void deleteFailNotFound() {
        //there will never be id 0 in repo.
        Response response = given()
                .when()
                .delete("api/bookings/0")
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Booking for given id not found!");
    }

    @Test
    public void validateBookingSportObjectNotFound() {
        Booking booking = prepareObjectsForBooking();
        int sportObjectId = booking.getSportObjectId(); // we will need to delete created sportObject
        booking.setSportObjectId(0);// there will never be id 0 in repository

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(booking)
                .post("/api/bookings")
                .then()
                .statusCode(404)
                .extract().response();
        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Sport object for given id not found");

        for (String rentEquipmentName : booking.getRentEquipmentNames()) {
            clearRentEquipmentAfterTest(rentEquipmentService.findByName(rentEquipmentName).getId());
        }
        clearAthleticsTrackAfterTest(sportObjectId);

    }

    @Test
    public void validateBookingRentEquipmentForSportObjectNotFound() {
        Booking booking = prepareObjectsForBooking();
        RentEquipment rentEquipmentNotInBooking = TestUtils.createRentEquipment();
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(rentEquipmentNotInBooking)
                .post("/api/rent_equipments")
                .then()
                .statusCode(201);

        booking.addRentEquipmentName(rentEquipmentNotInBooking.getName());

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(booking)
                .post("/api/bookings")
                .then()
                .statusCode(404)
                .extract().response();
        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Rent equipment with specified name not found in booked sport object");

        for (String rentEquipmentName : booking.getRentEquipmentNames()) {
            clearRentEquipmentAfterTest(rentEquipmentService.findByName(rentEquipmentName).getId());
        }
        clearAthleticsTrackAfterTest(booking.getSportObjectId());

    }

    @Test
    public void validateBookingWrongDatePast() {
        Booking booking = prepareObjectsForBooking();
        booking.setFromDate(LocalDateTime.of(2020,1,1,12,0));

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(booking)
                .post("/api/bookings")
                .then()
                .statusCode(400)
                .extract().response();
        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(actualExceptionMessage).isEqualTo("Date cannot be a past value");

        for (String rentEquipmentName : booking.getRentEquipmentNames()) {
            clearRentEquipmentAfterTest(rentEquipmentService.findByName(rentEquipmentName).getId());
        }
        clearAthleticsTrackAfterTest(booking.getSportObjectId());

    }

    @Test
    public void validateBookingWrongDateMinutesNot30Or00() {
        Booking booking = prepareObjectsForBooking();
        booking.setFromDate(LocalDateTime.of(2025,1,1,12,15)); // minutes - 15; should be 00 or 30

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(booking)
                .post("/api/bookings")
                .then()
                .statusCode(400)
                .extract().response();
        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(actualExceptionMessage).isEqualTo("Time in the date must be a full hour or half past hour");

        for (String rentEquipmentName : booking.getRentEquipmentNames()) {
            clearRentEquipmentAfterTest(rentEquipmentService.findByName(rentEquipmentName).getId());
        }
        clearAthleticsTrackAfterTest(booking.getSportObjectId());

    }

    @Test
    public void validateBookingIfNewBookingConflictWithExisting() {
        Booking bookingDefault = prepareObjectsForBooking();
        bookingDefault.setFromDate(LocalDateTime.of(2024,1,1,12,0)); // default date to tests: 1.01.2024, 12:00
        Booking savedDefault = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookingDefault)
                .post("/api/bookings")
                .then()
                .statusCode(201)
                .extract().as(Booking.class);

        //All bookings in this test have object: AthleticsTrack and hours:3 (this is important in checking if book should/should not be added)
        //Clone is used because only date is field which we are validating, other can be the same for every booking
        Booking bookingBeforeNotRight = bookingDefault.clone();
        bookingBeforeNotRight.setFromDate(LocalDateTime.of(2024,1,1,10,0)); // 10:00-13:00 existing booking is 12:00-15:00
        Response responseBeforeNotRight = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookingBeforeNotRight)
                .post("/api/bookings")
                .then()
                .statusCode(403)
                .extract().response();
        String responseMessageBeforeNotRight = responseBeforeNotRight.getBody().asString();
        String actualExceptionMessageBefore = TestUtils.getActualExceptionMessage(responseMessageBeforeNotRight);
        assertThat(responseBeforeNotRight.statusCode()).isEqualTo(403);
        assertThat(actualExceptionMessageBefore).isEqualTo("There is already booking for this object in given time");

        bookingBeforeNotRight.setRentEquipmentNames(Arrays.asList("123","456"));

        Booking bookingAfterNotRight = bookingDefault.clone();
        bookingAfterNotRight.setFromDate(LocalDateTime.of(2024,1,1,14,0)); // 14:00-17:00 existing booking is 12:00-15:00
        Response responseAfterNotRight = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookingAfterNotRight)
                .post("/api/bookings")
                .then()
                .statusCode(403)
                .extract().response();
        String responseMessageAfterNotRight = responseAfterNotRight.getBody().asString();
        String actualExceptionMessageAfter = TestUtils.getActualExceptionMessage(responseMessageAfterNotRight);
        assertThat(responseAfterNotRight.statusCode()).isEqualTo(403);
        assertThat(actualExceptionMessageAfter).isEqualTo("There is already booking for this object in given time");


        Booking bookingInsideNotRight = bookingDefault.clone();
        bookingInsideNotRight.setHours(2);
        bookingInsideNotRight.setFromDate(LocalDateTime.of(2024,1,1,12,0)); // 12:00-14:00 existing booking is 12:00-15:00
        Response responseInsideNotRight = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookingInsideNotRight)
                .post("/api/bookings")
                .then()
                .statusCode(403)
                .extract().response();
        String responseMessage = responseInsideNotRight.getBody().asString();
        String actualExceptionMessageInside = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(responseInsideNotRight.statusCode()).isEqualTo(403);
        assertThat(actualExceptionMessageInside).isEqualTo("There is already booking for this object in given time");


        Booking bookingBeforeRight = bookingDefault.clone();
        bookingBeforeRight.setFromDate(LocalDateTime.of(2024,1,1,9,0)); // 9:00-12:00 existing booking is 12:00-15:00, so ok
        Booking savedBeforeRight = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookingBeforeRight)
                .post("/api/bookings")
                .then()
                .statusCode(201)
                .extract().as(Booking.class);
        assertThat(savedBeforeRight.getId()).isNotNull();


        Booking bookingAfterRight = bookingDefault.clone();
        bookingAfterRight.setFromDate(LocalDateTime.of(2024,1,1,15,0)); //15:00-18:00 existing booking is 12:00-15:00, so ok
        Booking savedAfterRight = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookingAfterRight)
                .post("/api/bookings")
                .then()
                .statusCode(201)
                .extract().as(Booking.class);
        assertThat(savedAfterRight.getId()).isNotNull();


        Booking bookingConflictTimeButOtherObject = prepareObjectsForBooking(); //should pass
        bookingConflictTimeButOtherObject.setFromDate(LocalDateTime.of(2024,1,1,12,0)); //12:00-15:00 existing booking is 12:00-15:00, but object with other id
        Booking savedConflictTimeButOtherObject = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookingConflictTimeButOtherObject)
                .post("/api/bookings")
                .then()
                .statusCode(201)
                .extract().as(Booking.class);
        assertThat(savedConflictTimeButOtherObject.getId()).isNotNull();


        clearBookingAfterTest(savedDefault.getId());
        clearBookingAfterTest(savedBeforeRight.getId());
        clearBookingAfterTest(savedAfterRight.getId());
        clearBookingAfterTest(savedConflictTimeButOtherObject.getId());


        BookingEntity bookingEntityDefault = bookingMapper.toEntity(savedDefault);
        for (RentEquipmentEntity rentEquipmentEntity : bookingEntityDefault.getRentEquipment()) {
            clearRentEquipmentAfterTest(rentEquipmentEntity.getId());
        }
        clearAthleticsTrackAfterTest(bookingEntityDefault.getSportObject().getId());

        BookingEntity bookingEntityConflictTimeButOtherObject = bookingMapper.toEntity(savedConflictTimeButOtherObject);
        for (RentEquipmentEntity rentEquipmentEntity : bookingEntityConflictTimeButOtherObject.getRentEquipment()) {
            clearRentEquipmentAfterTest(rentEquipmentEntity.getId());
        }
        clearAthleticsTrackAfterTest(bookingEntityConflictTimeButOtherObject.getSportObject().getId());


    }

    private Booking prepareObjectsForBooking() {
        AthleticsTrack athleticsTrack = TestUtils.createAthleticsTrack();
        RentEquipment rentEquipment1 = TestUtils.createRentEquipment();
        RentEquipment rentEquipment2 = TestUtils.createRentEquipment();

        AthleticsTrack savedTrack = given()
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


    private void clearBookingAfterTest(Integer id) {
        bookingService.deleteBookingById(id);
    }

    private void clearRentEquipmentAfterTest(Integer id) {
        rentEquipmentService.deleteRentEquipmentById(id);
    }

    private void clearAthleticsTrackAfterTest(Integer id) {
        sportObjectService.deleteSportObjectById(id);
    }


}
