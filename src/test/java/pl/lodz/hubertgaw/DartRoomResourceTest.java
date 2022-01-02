package pl.lodz.hubertgaw;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.lodz.hubertgaw.dto.DartRoom;
import pl.lodz.hubertgaw.service.SportObjectService;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class DartRoomResourceTest {

    @Inject
    SportObjectService sportObjectService;

    @Test
    public void getAll() {
        given()
                .when().get("/api/dart_rooms")
                .then()
                .statusCode(200);
    }

    @Test
    public void getById() {
        DartRoom dartRoom = createDartRoom();
        DartRoom saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dartRoom)
                .post("/api/dart_rooms")
                .then()
                .statusCode(201)
                .extract().as(DartRoom.class);
        DartRoom got = given()
                .when().get("/api/dart_rooms/{dartRoomId}", saved.getId())
                .then()
                .statusCode(200)
                .extract().as(DartRoom.class);
        assertThat(saved).isEqualTo(got);
        clearDartRoomAfterTest(saved.getId());
//        assertThat(saved.equals(got)).isTrue();
    }

    @Test
    public void post() {
        DartRoom dartRoom = createDartRoom();
        DartRoom saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dartRoom)
                .post("/api/dart_rooms")
                .then()
                .statusCode(201)
                .extract().as(DartRoom.class);
        assertThat(saved.getId()).isNotNull();
        clearDartRoomAfterTest(saved.getId());
    }

    @Test
    public void postFailNoName() {
        DartRoom dartRoom = createDartRoom();
        dartRoom.setName(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dartRoom)
                .post("/api/dart_rooms")
                .then()
                .statusCode(400);
    }

    @Test
    public void put() {
        DartRoom dartRoom = createDartRoom();
        DartRoom saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dartRoom)
                .post("/api/dart_rooms")
                .then()
                .statusCode(201)
                .extract().as(DartRoom.class);
        saved.setName("Updated");
        DartRoom updated = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/dart_rooms")
                .then()
                .statusCode(200)
                .extract().as(DartRoom.class);
        assertThat(updated.getName()).isEqualTo("Updated");
        clearDartRoomAfterTest(saved.getId());

    }

    @Test
    public void putFailStandsNumber() {
        DartRoom dartRoom = createDartRoom();
        DartRoom saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dartRoom)
                .post("/api/dart_rooms")
                .then()
                .statusCode(201)
                .extract().as(DartRoom.class);
        saved.setStandsNumber(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/dart_rooms")
                .then()
                .statusCode(400);
        clearDartRoomAfterTest(saved.getId());
    }

    private DartRoom createDartRoom() {
        DartRoom dartRoom = new DartRoom();
        dartRoom.setName(RandomStringUtils.randomAlphabetic(10));
        dartRoom.setFullPrice(Double.valueOf(RandomStringUtils.randomNumeric(2)));
        dartRoom.setStandsNumber(Integer.valueOf(RandomStringUtils.randomNumeric(2)));
        dartRoom.setStandPrice(Double.valueOf(RandomStringUtils.randomNumeric(1)));
        return dartRoom;
    }

    private void clearDartRoomAfterTest(Integer id) {
        sportObjectService.deleteSportObjectById(id);
    }

}
