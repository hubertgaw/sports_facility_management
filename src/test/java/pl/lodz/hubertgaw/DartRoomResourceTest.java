package pl.lodz.hubertgaw;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.lodz.hubertgaw.dto.DartRoom;
import pl.lodz.hubertgaw.dto.DartRoom;
import pl.lodz.hubertgaw.dto.RentEquipment;
import pl.lodz.hubertgaw.service.RentEquipmentService;
import pl.lodz.hubertgaw.service.SportObjectService;
import pl.lodz.hubertgaw.utils.TestUtils;

import javax.inject.Inject;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static pl.lodz.hubertgaw.utils.TestUtils.createDartRoom;

@QuarkusTest
@TestSecurity(authorizationEnabled = false)
public class DartRoomResourceTest {

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
        TestUtils.clearSportObjectAfterTest(saved.getId());
//        assertThat(saved.equals(got)).isTrue();
    }

    @Test
    public void getByIdFailNotFound() {
        Response response = given()
                .when().get("/api/dart_rooms/{dartRoomId}", 0)// there will be for sure no object with id 0
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Dart room for given id not found. Try to search in all sport objects or change the id.");

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
        TestUtils.clearSportObjectAfterTest(saved.getId());
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
        TestUtils.clearSportObjectAfterTest(saved.getId());

    }

    @Test
    public void putFailNoStandsNumber() {
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
        TestUtils.clearSportObjectAfterTest(saved.getId());
    }

    @Test
    public void postFailDuplicateName() {
        DartRoom dartRoom = TestUtils.createDartRoom();
        DartRoom saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dartRoom)
                .post("/api/dart_rooms")
                .then()
                .extract().as(DartRoom.class);

        DartRoom dartRoom2 = TestUtils.createDartRoom();
        dartRoom2.setName(dartRoom.getName());

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dartRoom2)
                .post("/api/dart_rooms")
                .then()
                .statusCode(403)
                .extract().response();
        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(403);
        assertThat(actualExceptionMessage).isEqualTo("Sport object with given name already exists");

        TestUtils.clearSportObjectAfterTest(saved.getId());
    }

    @Test
    public void putFailEmptyId() {
        DartRoom dartRoom = TestUtils.createDartRoom();
        DartRoom saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dartRoom)
                .post("/api/dart_rooms")
                .then()
                .statusCode(201)
                .extract().as(DartRoom.class);
        int id = saved.getId(); // we will need that to delete after test.
        saved.setId(null);
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/dart_rooms")
                .then()
                .statusCode(400)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(actualExceptionMessage).isEqualTo("Id for updating dart room cannot be null");

        TestUtils.clearSportObjectAfterTest(id);
    }

    @Test
    public void putFailNotFound() {
        DartRoom dartRoom = TestUtils.createDartRoom();
        //there will never be id 0 in repo.
        dartRoom.setId(0);
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dartRoom)
                .put("/api/dart_rooms")
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Dart room for given id not found. Try to search in all sport objects or change the id.");
    }

    @Test
    public void putEquipmentToDartRoom() {
        DartRoom dartRoom = TestUtils.createDartRoom();
        RentEquipment rentEquipment = TestUtils.createRentEquipment();

        DartRoom savedTrack = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dartRoom)
                .post("/api/dart_rooms")
                .then()
                .statusCode(201)
                .extract().as(DartRoom.class);

        RentEquipment savedEquipment = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(rentEquipment)
                .post("/api/rent_equipments")
                .then()
                .statusCode(201)
                .extract().as(RentEquipment.class);

        DartRoom trackAfterPut = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dartRoom)
                .put("/api/dart_rooms/{dartRoomId}/rent_equipment/{rentEquipmentId}",
                        savedTrack.getId(), savedEquipment.getId())
                .then()
                .statusCode(200)
                .extract().as(DartRoom.class);

        //compare name of rent equipment
        assertThat(new ArrayList<>(trackAfterPut.getRentEquipmentNames()).get(0)).isEqualTo(savedEquipment.getName());

        TestUtils.clearRentEquipmentAfterTest(savedEquipment.getId());
        TestUtils.clearSportObjectAfterTest(savedTrack.getId());
    }

    @Test
    public void putEquipmentToDartRoomDartRoomNotFound() {
        DartRoom dartRoom = TestUtils.createDartRoom();
        RentEquipment rentEquipment = TestUtils.createRentEquipment();

        RentEquipment savedEquipment = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(rentEquipment)
                .post("/api/rent_equipments")
                .then()
                .statusCode(201)
                .extract().as(RentEquipment.class);

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dartRoom)
                .put("/api/dart_rooms/0/rent_equipment/{rentEquipmentId}",
                        savedEquipment.getId())
                .then()
                .statusCode(404)
                .extract().response();

        //compare name of rent equipment
//        assertThat(new ArrayList<>(trackAfterPut.getRentEquipmentNames()).get(0)).isEqualTo(savedEquipment.getName());

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Dart room for given id not found. Try to search in all sport objects or change the id.");


        TestUtils.clearRentEquipmentAfterTest(savedEquipment.getId());
//        TestUtils.clearSportObjectAfterTest(savedTrack.getId());
    }

    @Test
    public void putEquipmentToDartRoomRentEquipmentNotFound() {
        DartRoom dartRoom = TestUtils.createDartRoom();

        DartRoom savedTrack = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dartRoom)
                .post("/api/dart_rooms")
                .then()
                .statusCode(201)
                .extract().as(DartRoom.class);

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dartRoom)
                .put("/api/dart_rooms/{dartRoomId}/rent_equipment/0",
                        savedTrack.getId())
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Rent equipment for given id not found!");

        TestUtils.clearSportObjectAfterTest(savedTrack.getId());
    }
}
