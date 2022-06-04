package pl.lodz.hubertgaw;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import pl.lodz.hubertgaw.dto.Booking;
import pl.lodz.hubertgaw.dto.RentEquipment;
import pl.lodz.hubertgaw.mapper.BookingMapper;
import pl.lodz.hubertgaw.repository.entity.BookingEntity;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.service.RentEquipmentService;
import pl.lodz.hubertgaw.utils.TestUtils;

import javax.inject.Inject;

import java.time.LocalDateTime;
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@TestSecurity(authorizationEnabled = false)
public class BookingResourceTest {

    @Inject
    RentEquipmentService rentEquipmentService;

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
        Booking booking = TestUtils.prepareObjectsWithCapacityForBooking(10);

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
        TestUtils.clearBookingAfterTest(savedBooking.getId());
        BookingEntity bookingEntity = bookingMapper.toEntity(savedBooking);
//        for (RentEquipmentEntity rentEquipmentEntity : bookingEntity.getRentEquipment()) {
//            TestUtils.clearRentEquipmentAfterTest(rentEquipmentEntity.getId());
//        }
        TestUtils.clearSportObjectAfterTest(bookingEntity.getSportObject().getId());
    }

    @Test
    public void post() {
        Booking booking = TestUtils.prepareObjectsWithCapacityForBooking(10);

        Booking saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(booking)
                .post("/api/bookings")
                .then()
                .statusCode(201)
                .extract().as(Booking.class);
        assertThat(saved.getId()).isNotNull();
        TestUtils.clearBookingAfterTest(saved.getId());
        BookingEntity bookingEntity = bookingMapper.toEntity(saved);
//        for (RentEquipmentEntity rentEquipmentEntity : bookingEntity.getRentEquipment()) {
//            TestUtils.clearRentEquipmentAfterTest(rentEquipmentEntity.getId());
//        }
        TestUtils.clearSportObjectAfterTest(bookingEntity.getSportObject().getId());

    }

    @Test
    public void put() {

        Booking booking = TestUtils.prepareObjectsWithCapacityForBooking(10);
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
        TestUtils.clearBookingAfterTest(saved.getId());
        BookingEntity bookingEntity = bookingMapper.toEntity(saved);
//        for (RentEquipmentEntity rentEquipmentEntity : bookingEntity.getRentEquipment()) {
//            TestUtils.clearRentEquipmentAfterTest(rentEquipmentEntity.getId());
//        }
        TestUtils.clearSportObjectAfterTest(bookingEntity.getSportObject().getId());


    }


    @Test
    public void putFailNotFound() {
        Booking booking = TestUtils.prepareObjectsWithCapacityForBooking(10);

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

//        for (String rentEquipmentName : booking.getRentEquipmentNames()) {
//            TestUtils.clearRentEquipmentAfterTest(rentEquipmentService.findByName(rentEquipmentName).getId());
//        }
        TestUtils.clearSportObjectAfterTest(booking.getSportObjectId());

    }

    @Test
    public void putFailEmptyId() {
        Booking booking = TestUtils.prepareObjectsWithCapacityForBooking(10);
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

        TestUtils.clearBookingAfterTest(id);
        BookingEntity bookingEntity = bookingMapper.toEntity(saved);
//        for (RentEquipmentEntity rentEquipmentEntity : bookingEntity.getRentEquipment()) {
//            TestUtils.clearRentEquipmentAfterTest(rentEquipmentEntity.getId());
//        }
        TestUtils.clearSportObjectAfterTest(bookingEntity.getSportObject().getId());
    }

    @Test
    public void delete() {
        Booking booking = TestUtils.prepareObjectsWithCapacityForBooking(10);
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
//        for (RentEquipmentEntity rentEquipmentEntity : bookingEntity.getRentEquipment()) {
//            TestUtils.clearRentEquipmentAfterTest(rentEquipmentEntity.getId());
//        }
        TestUtils.clearSportObjectAfterTest(bookingEntity.getSportObject().getId());
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
        Booking booking = TestUtils.prepareObjectsWithCapacityForBooking(10);
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

//        for (String rentEquipmentName : booking.getRentEquipmentNames()) {
//            TestUtils.clearRentEquipmentAfterTest(rentEquipmentService.findByName(rentEquipmentName).getId());
//        }
        TestUtils.clearSportObjectAfterTest(sportObjectId);

    }

    @Test
    public void validateBookingRentEquipmentForSportObjectNotFound() {
        Booking booking = TestUtils.prepareObjectsWithCapacityForBooking(10);
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

//        for (String rentEquipmentName : booking.getRentEquipmentNames()) {
//            TestUtils.clearRentEquipmentAfterTest(rentEquipmentService.findByName(rentEquipmentName).getId());
//        }
        TestUtils.clearSportObjectAfterTest(booking.getSportObjectId());

    }

    @Test
    public void validateBookingWrongDatePast() {
        Booking booking = TestUtils.prepareObjectsWithCapacityForBooking(10);
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

//        for (String rentEquipmentName : booking.getRentEquipmentNames()) {
//            TestUtils.clearRentEquipmentAfterTest(rentEquipmentService.findByName(rentEquipmentName).getId());
//        }
        TestUtils.clearSportObjectAfterTest(booking.getSportObjectId());

    }

    @Test
    public void validateBookingWrongDateMinutesNot30Or00() {
        Booking booking = TestUtils.prepareObjectsWithCapacityForBooking(10);
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

//        for (String rentEquipmentName : booking.getRentEquipmentNames()) {
//            TestUtils.clearRentEquipmentAfterTest(rentEquipmentService.findByName(rentEquipmentName).getId());
//        }
        TestUtils.clearSportObjectAfterTest(booking.getSportObjectId());

    }

    // in this test there will be validation of booking based on time
    // (for example booking is at 12:00 for 2 hours so there cannot be any
    // booking from 12:00 to 14:00) we use object with getHalfRent set to false
    // and with no capacity field, because in this type of object there can be
    // only one booking in a time (in bookings for objects with capacity there
    // can be more bookings than one in a time).
    @Test
    public void validateBookingBasedOnTime() {
        Booking bookingDefault = TestUtils.prepareObjectsWithHalfRentForBooking(false);
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

        // Booking with the same time as previous, but it is another object so booking can be added
        Booking bookingConflictTimeButOtherObject = TestUtils.prepareObjectsWithCapacityForBooking(10); //should pass
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


        TestUtils.clearBookingAfterTest(savedDefault.getId());
        TestUtils.clearBookingAfterTest(savedBeforeRight.getId());
        TestUtils.clearBookingAfterTest(savedAfterRight.getId());
        TestUtils.clearBookingAfterTest(savedConflictTimeButOtherObject.getId());


        BookingEntity bookingEntityDefault = bookingMapper.toEntity(savedDefault);
//        for (RentEquipmentEntity rentEquipmentEntity : bookingEntityDefault.getRentEquipment()) {
//            TestUtils.clearRentEquipmentAfterTest(rentEquipmentEntity.getId());
//        }
        TestUtils.clearSportObjectAfterTest(bookingEntityDefault.getSportObject().getId());

        BookingEntity bookingEntityConflictTimeButOtherObject = bookingMapper.toEntity(savedConflictTimeButOtherObject);
//        for (RentEquipmentEntity rentEquipmentEntity : bookingEntityConflictTimeButOtherObject.getRentEquipment()) {
//            TestUtils.clearRentEquipmentAfterTest(rentEquipmentEntity.getId());
//        }
        TestUtils.clearSportObjectAfterTest(bookingEntityConflictTimeButOtherObject.getSportObject().getId());

    }

    // in this test we are checking if booking's validation based on halfRent property
    // works. If that property in object is set to true there can be 2 bookings at the time
    // (if in booking that property is also set to true). In previous test we checked how it
    // works if property in object is set to false.
    @Test
    public void validateBookingBasedOnHalfRentTrueAndExistedBookingIsHalfRentTrue() {
        Booking bookingDefault = TestUtils.prepareObjectsWithHalfRentForBooking(true);
        bookingDefault.setFromDate(LocalDateTime.of(2024, 1, 1, 12, 0)); // default date to tests: 1.01.2024, 12:00
        // when that property is set to true there can be 2 bookings at the same time
        bookingDefault.setHalfRent(true);
        //first booking
        Booking savedDefault = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookingDefault)
                .post("/api/bookings")
                .then()
                .statusCode(201)
                .extract().as(Booking.class);
        assertThat(savedDefault).isNotNull();

        // second booking at the same time - it should be added
        Booking secondBooking = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookingDefault)
                .post("/api/bookings")
                .then()
                .statusCode(201)
                .extract().as(Booking.class);
        assertThat(secondBooking).isNotNull();

        // third booking at the same time - it shouldn't be added
        Response tooMuchBookingsResponse = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookingDefault)
                .post("/api/bookings")
                .then()
                .statusCode(403)
                .extract().response();
        String responseMessageHalfRentTrue = tooMuchBookingsResponse.getBody().asString();
        String actualExceptionMessageHalfRentTrue = TestUtils.getActualExceptionMessage(responseMessageHalfRentTrue);
        assertThat(tooMuchBookingsResponse.statusCode()).isEqualTo(403);
        assertThat(actualExceptionMessageHalfRentTrue).isEqualTo("There is already booking for this object in given time");


        Booking bookingWithObjectHalfRentFalse = TestUtils.prepareObjectsWithHalfRentForBooking(false);
        bookingWithObjectHalfRentFalse.setFromDate(LocalDateTime.of(2024, 1, 3, 12, 0));
        bookingWithObjectHalfRentFalse.setHalfRent(true);
        Response wrongHalfRentResponse = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookingWithObjectHalfRentFalse)
                .post("/api/bookings")
                .then()
                .statusCode(400)
                .extract().response();
        String responseMessageWrongHalfRent = wrongHalfRentResponse.getBody().asString();
        String actualExceptionWrongHalfRent = TestUtils.getActualExceptionMessage(responseMessageWrongHalfRent);
        assertThat(wrongHalfRentResponse.statusCode()).isEqualTo(400);
        assertThat(actualExceptionWrongHalfRent).isEqualTo("Field half rented cannot be applicable to provided object");

        TestUtils.clearBookingAfterTest(savedDefault.getId());
        TestUtils.clearBookingAfterTest(secondBooking.getId());
    }

    @Test
    public void validateBookingBasedOnHalfRentTrueAndExistedBookingIsHalfRentFalse() {
        Booking bookingDefault = TestUtils.prepareObjectsWithHalfRentForBooking(true);
        bookingDefault.setFromDate(LocalDateTime.of(2024, 1, 1, 12, 0)); // default date to tests: 1.01.2024, 12:00
        // we set it to false, so there can be only one booking at the time (even if in object halfRent is set to true)
        bookingDefault.setHalfRent(false);
        //first booking
        Booking savedDefault = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookingDefault)
                .post("/api/bookings")
                .then()
                .statusCode(201)
                .extract().as(Booking.class);
        assertThat(savedDefault).isNotNull();

        // second booking at the same time - it should not be added (even for half rent)
        Response tooMuchBookingsResponse = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookingDefault)
                .post("/api/bookings")
                .then()
                .statusCode(403)
                .extract().response();
        String responseMessage = tooMuchBookingsResponse.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(tooMuchBookingsResponse.statusCode()).isEqualTo(403);
        assertThat(actualExceptionMessage).isEqualTo("There is already booking for this object in given time");

        bookingDefault.setHalfRent(true);
        Response tooMuchBookingsResponseWithHalfRent = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookingDefault)
                .post("/api/bookings")
                .then()
                .statusCode(403)
                .extract().response();
        String responseMessageWithHalfRent = tooMuchBookingsResponseWithHalfRent.getBody().asString();
        String actualExceptionMessageWithHalfRent = TestUtils.getActualExceptionMessage(responseMessageWithHalfRent);
        assertThat(tooMuchBookingsResponseWithHalfRent.statusCode()).isEqualTo(403);
        assertThat(actualExceptionMessageWithHalfRent).isEqualTo("There is already booking for this object in given time");


        TestUtils.clearBookingAfterTest(savedDefault.getId());
    }


    @Test
    public void validateBookingBasedOnCapacity() {
        Booking bookingDefault = TestUtils.prepareObjectsWithCapacityForBooking(10);
        bookingDefault.setFromDate(LocalDateTime.of(2024, 1, 1, 12, 0)); // default date to tests: 1.01.2024, 12:00
        bookingDefault.setNumberOfPlaces(5);
        //first booking
        Booking savedDefault = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookingDefault)
                .post("/api/bookings")
                .then()
                .statusCode(201)
                .extract().as(Booking.class);
        assertThat(savedDefault).isNotNull();

        bookingDefault.setNumberOfPlaces(4);
        // second booking at the same time - it should be added (numberOfPlaces 9/10)
        Booking secondBooking = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookingDefault)
                .post("/api/bookings")
                .then()
                .statusCode(201)
                .extract().as(Booking.class);
        assertThat(secondBooking).isNotNull();

        bookingDefault.setNumberOfPlaces(1);
        // third booking at the same time - it should be added (numberOfPlaces 10/10)
        Booking thirdBooking = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookingDefault)
                .post("/api/bookings")
                .then()
                .statusCode(201)
                .extract().as(Booking.class);
        assertThat(thirdBooking).isNotNull();

        // fourth booking at the same time - it shouldn't be added (too much numberOfPlaces)
        Response tooMuchBookingsResponse = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookingDefault)
                .post("/api/bookings")
                .then()
                .statusCode(403)
                .extract().response();
        String responseMessageHalfRentTrue = tooMuchBookingsResponse.getBody().asString();
        String actualExceptionMessageHalfRentTrue = TestUtils.getActualExceptionMessage(responseMessageHalfRentTrue);
        assertThat(tooMuchBookingsResponse.statusCode()).isEqualTo(403);
        assertThat(actualExceptionMessageHalfRentTrue).isEqualTo("There is already booking for this object in given time");

    }

    @Test
    public void validateBookingHalfRentAndCapacityBothSpecified() {
        Booking bookingWithHalfRentAndCapacity = TestUtils.prepareObjectsWithHalfRentForBooking(true);
        bookingWithHalfRentAndCapacity.setFromDate(LocalDateTime.of(2024, 1, 3, 12, 0));
        bookingWithHalfRentAndCapacity.setHalfRent(true);
        bookingWithHalfRentAndCapacity.setNumberOfPlaces(10);
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookingWithHalfRentAndCapacity)
                .post("/api/bookings")
                .then()
                .statusCode(400)
                .extract().response();
        String responseMessageWrongHalfRent = response.getBody().asString();
        String actualExceptionWrongHalfRent = TestUtils.getActualExceptionMessage(responseMessageWrongHalfRent);
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(actualExceptionWrongHalfRent).isEqualTo("Number of people cannot be provided along with half rent");

    }

    @Test
    public void validateBookingCapacityNotAllowedButSpecified() {
        Booking bookingWithCapacity = TestUtils.prepareObjectsWithHalfRentForBooking(true); // With half Rent means capacity is null
        bookingWithCapacity.setFromDate(LocalDateTime.of(2024, 1, 3, 12, 0));
        bookingWithCapacity.setNumberOfPlaces(10);
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bookingWithCapacity)
                .post("/api/bookings")
                .then()
                .statusCode(400)
                .extract().response();
        String responseMessageWrongHalfRent = response.getBody().asString();
        String actualExceptionWrongHalfRent = TestUtils.getActualExceptionMessage(responseMessageWrongHalfRent);
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(actualExceptionWrongHalfRent).isEqualTo("Field number of places cannot be applicable to provided object");

    }


}
