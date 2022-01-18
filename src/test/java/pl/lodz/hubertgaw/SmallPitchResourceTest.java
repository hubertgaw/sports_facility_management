package pl.lodz.hubertgaw;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.lodz.hubertgaw.dto.SmallPitch;
import pl.lodz.hubertgaw.dto.RentEquipment;
import pl.lodz.hubertgaw.dto.SmallPitch;
import pl.lodz.hubertgaw.service.RentEquipmentService;
import pl.lodz.hubertgaw.service.SportObjectService;
import pl.lodz.hubertgaw.utils.TestUtils;

import javax.inject.Inject;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static pl.lodz.hubertgaw.utils.TestUtils.createSmallPitch;

@QuarkusTest
public class SmallPitchResourceTest {

    @Inject
    SportObjectService sportObjectService;
    @Inject
    RentEquipmentService rentEquipmentService;

    @Test
    public void getAll() {
        given()
                .when().get("/api/small_pitches")
                .then()
                .statusCode(200);
    }

    @Test
    public void getById() {
        SmallPitch smallPitch = createSmallPitch();
        SmallPitch saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(smallPitch)
                .post("/api/small_pitches")
                .then()
                .statusCode(201)
                .extract().as(SmallPitch.class);
        SmallPitch got = given()
                .when().get("/api/small_pitches/{smallPitchId}", saved.getId())
                .then()
                .statusCode(200)
                .extract().as(SmallPitch.class);
        assertThat(saved).isEqualTo(got);
        clearSmallPitchAfterTest(saved.getId());
//        assertThat(saved.equals(got)).isTrue();
    }

    @Test
    public void post() {
        SmallPitch smallPitch = createSmallPitch();
        SmallPitch saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(smallPitch)
                .post("/api/small_pitches")
                .then()
                .statusCode(201)
                .extract().as(SmallPitch.class);
        assertThat(saved.getId()).isNotNull();
        clearSmallPitchAfterTest(saved.getId());
    }

    @Test
    public void postFailNoName() {
        SmallPitch smallPitch = createSmallPitch();
        smallPitch.setName(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(smallPitch)
                .post("/api/small_pitches")
                .then()
                .statusCode(400);
    }

    @Test
    public void put() {
        SmallPitch smallPitch = createSmallPitch();
        SmallPitch saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(smallPitch)
                .post("/api/small_pitches")
                .then()
                .statusCode(201)
                .extract().as(SmallPitch.class);
        saved.setName("Updated");
        SmallPitch updated = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/small_pitches")
                .then()
                .statusCode(200)
                .extract().as(SmallPitch.class);
        assertThat(updated.getName()).isEqualTo("Updated");
        clearSmallPitchAfterTest(saved.getId());
    }

    @Test
    public void putIsFullRentedNull() {
        SmallPitch smallPitch = createSmallPitch();
        SmallPitch saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(smallPitch)
                .post("/api/small_pitches")
                .then()
                .statusCode(201)
                .extract().as(SmallPitch.class);
        saved.setIsFullRented(null);
        SmallPitch updated = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/small_pitches")
                .then()
                .statusCode(200)
                .extract().as(SmallPitch.class);
        assertThat(updated.getIsFullRented()).isEqualTo(null);
        clearSmallPitchAfterTest(saved.getId());
    }


    @Test
    public void putFailNoHalfPitchPrice() {
        SmallPitch smallPitch = createSmallPitch();
        SmallPitch saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(smallPitch)
                .post("/api/small_pitches")
                .then()
                .statusCode(201)
                .extract().as(SmallPitch.class);
        saved.setHalfPitchPrice(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/small_pitches")
                .then()
                .statusCode(400);
        clearSmallPitchAfterTest(saved.getId());
    }

    @Test
    public void postFailDuplicateName() {
        SmallPitch smallPitch = TestUtils.createSmallPitch();
        SmallPitch saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(smallPitch)
                .post("/api/small_pitches")
                .then()
                .extract().as(SmallPitch.class);

        SmallPitch smallPitch2 = TestUtils.createSmallPitch();
        smallPitch2.setName(smallPitch.getName());

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(smallPitch2)
                .post("/api/small_pitches")
                .then()
                .statusCode(403)
                .extract().response();
        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(403);
        assertThat(actualExceptionMessage).isEqualTo("Sport object with given name already exists");

        clearSmallPitchAfterTest(saved.getId());
    }

    @Test
    public void putFailEmptyId() {
        SmallPitch smallPitch = TestUtils.createSmallPitch();
        SmallPitch saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(smallPitch)
                .post("/api/small_pitches")
                .then()
                .statusCode(201)
                .extract().as(SmallPitch.class);
        int id = saved.getId(); // we will need that to delete after test.
        saved.setId(null);
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/small_pitches")
                .then()
                .statusCode(400)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(actualExceptionMessage).isEqualTo("Id for updating small pitch cannot be null");

        clearSmallPitchAfterTest(id);
    }

    @Test
    public void putFailNotFound() {
        SmallPitch smallPitch = TestUtils.createSmallPitch();
        //there will never be id 0 in repo.
        smallPitch.setId(0);
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(smallPitch)
                .put("/api/small_pitches")
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Small pitch for given id not found. Try to search in all sport objects or change the id.");
    }

    @Test
    public void putEquipmentToSmallPitch() {
        SmallPitch smallPitch = TestUtils.createSmallPitch();
        RentEquipment rentEquipment = TestUtils.createRentEquipment();

        SmallPitch savedTrack = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(smallPitch)
                .post("/api/small_pitches")
                .then()
                .statusCode(201)
                .extract().as(SmallPitch.class);

        RentEquipment savedEquipment = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(rentEquipment)
                .post("/api/rent_equipments")
                .then()
                .statusCode(201)
                .extract().as(RentEquipment.class);

        SmallPitch trackAfterPut = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(smallPitch)
                .put("/api/small_pitches/{smallPitchId}/rent_equipment/{rentEquipmentId}",
                        savedTrack.getId(), savedEquipment.getId())
                .then()
                .statusCode(200)
                .extract().as(SmallPitch.class);

        //compare name of rent equipment
        assertThat(new ArrayList<>(trackAfterPut.getRentEquipmentNames()).get(0)).isEqualTo(savedEquipment.getName());

        clearRentEquipmentAfterTest(savedEquipment.getId());
        clearSmallPitchAfterTest(savedTrack.getId());
    }

    @Test
    public void putEquipmentToSmallPitchSmallPitchNotFound() {
        SmallPitch smallPitch = TestUtils.createSmallPitch();
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
                .body(smallPitch)
                .put("/api/small_pitches/0/rent_equipment/{rentEquipmentId}",
                        savedEquipment.getId())
                .then()
                .statusCode(404)
                .extract().response();

        //compare name of rent equipment
//        assertThat(new ArrayList<>(trackAfterPut.getRentEquipmentNames()).get(0)).isEqualTo(savedEquipment.getName());

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Small pitch for given id not found. Try to search in all sport objects or change the id.");


        clearRentEquipmentAfterTest(savedEquipment.getId());
//        clearSmallPitchAfterTest(savedTrack.getId());
    }

    @Test
    public void putEquipmentToSmallPitchRentEquipmentNotFound() {
        SmallPitch smallPitch = TestUtils.createSmallPitch();

        SmallPitch savedTrack = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(smallPitch)
                .post("/api/small_pitches")
                .then()
                .statusCode(201)
                .extract().as(SmallPitch.class);

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(smallPitch)
                .put("/api/small_pitches/{smallPitchId}/rent_equipment/0",
                        savedTrack.getId())
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Rent equipment for given id not found!");

        clearSmallPitchAfterTest(savedTrack.getId());
    }

    private void clearSmallPitchAfterTest(Integer id) {
        sportObjectService.deleteSportObjectById(id);
    }

    private void clearRentEquipmentAfterTest(Integer id) {
        rentEquipmentService.deleteRentEquipmentById(id);
    }
}
