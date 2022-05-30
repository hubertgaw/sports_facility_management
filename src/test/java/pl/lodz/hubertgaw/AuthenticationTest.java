package pl.lodz.hubertgaw;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import pl.lodz.hubertgaw.dto.Booking;
import pl.lodz.hubertgaw.dto.User;
import pl.lodz.hubertgaw.utils.TestUtils;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthenticationTest {

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    public void loggedAsAdminTest() {
        // permit all
        Booking booking = TestUtils.prepareObjectsWithCapacityForBooking(10);
        Booking saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(booking)
                .post("/api/bookings")
                .then()
                .statusCode(201)
                .extract().as(Booking.class);

        // only admin
        given()
                .when().get("/api/bookings")
                .then()
                .statusCode(200);

        // only user
        given()
                .when().get("/api/bookings/mine")
                .then()
                .statusCode(403); // FORBIDDEN

        // user and admin (only endpoints secured that way is putBooking and deleteBooking,
        // so we use booking created earlier
        given()
                .when()
                .delete("api/bookings/{bookingId}", saved.getId())
                .then()
                .statusCode(204);


    }

    @Test
    @TestSecurity(user = "user@mail.com", roles = "USER")
    public void loggedAsUserTest() {
        // permit all
//        Booking booking = TestUtils.prepareObjectsForBooking();
        given()
                .when().get("/api/athletics_tracks")
                .then()
                .statusCode(200);

        // only admin
        given()
                .when().get("/api/bookings")
                .then()
                .statusCode(403); //FORBIDDEN

        // only user
        // first: we create user
        User user = new User();
        user.setEmail("user@mail.com");
        user.setFirstName("user");
        user.setLastName("test");
        user.setPassword("test123");
        user.setPhoneNumber("123456789");
        User savedUser = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(user)
                .post("/api/users")
                .then()
                .statusCode(201)
                .extract().as(User.class);

        // then: test
        given()
                .when().get("/api/bookings/mine")
                .then()
                .statusCode(200);

        // user and admin (only endpoints secured that way is putBooking and deleteBooking,
        // so we use booking created earlier
//        given()
//                .when()
//                .delete("api/bookings/{bookingId}", saved.getId())
//                .then()
//                .statusCode(204);

        given()
                .when()
                .delete("api/bookings/{bookingId}", 0) // for sure that id not belongs to user so there will bo FORBIDDEN
                .then()
                .statusCode(403); //FORBIDDEN


    }

    @Test
    public void notLoggedTest() {
        // permit all
        given()
                .when().get("/api/athletics_tracks")
                .then()
                .statusCode(200);

        // only admin
        given()
                .when().get("/api/bookings")
                .then()
                .statusCode(401); //UNAUTHORIZED

        // only user
        given()
                .when().get("/api/bookings/mine")
                .then()
                .statusCode(401); // UNAUTHORIZED

        // user and admin (only endpoints secured that way is putBooking and deleteBooking
        given()
                .when()
                .delete("api/bookings/{bookingId}", 0)
                .then()
                .statusCode(401); //UNAUTHORIZED


    }

}
