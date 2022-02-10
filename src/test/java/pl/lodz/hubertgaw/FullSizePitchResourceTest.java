package pl.lodz.hubertgaw;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import pl.lodz.hubertgaw.dto.FullSizePitch;
import pl.lodz.hubertgaw.dto.RentEquipment;
import pl.lodz.hubertgaw.service.RentEquipmentService;
import pl.lodz.hubertgaw.service.SportObjectService;
import pl.lodz.hubertgaw.utils.TestUtils;

import javax.inject.Inject;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static pl.lodz.hubertgaw.utils.TestUtils.createFullSizePitch;

@QuarkusTest
public class FullSizePitchResourceTest {

    @Inject
    SportObjectService sportObjectService;
    @Inject
    RentEquipmentService rentEquipmentService;

    @Test
    public void getAll() {
        given()
                .when().get("/api/full_size_pitches")
                .then()
                .statusCode(200);
    }

    @Test
    public void getById() {
        FullSizePitch fullSizePitch = createFullSizePitch();
        FullSizePitch saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(fullSizePitch)
                .post("/api/full_size_pitches")
                .then()
                .statusCode(201)
                .extract().as(FullSizePitch.class);
        FullSizePitch got = given()
                .when().get("/api/full_size_pitches/{fullSizePitchId}", saved.getId())
                .then()
                .statusCode(200)
                .extract().as(FullSizePitch.class);
        assertThat(saved).isEqualTo(got);
        clearFullSizePitchAfterTest(saved.getId());
//        assertThat(saved.equals(got)).isTrue();
    }

    @Test
    public void post() {
        FullSizePitch fullSizePitch = createFullSizePitch();
        FullSizePitch saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(fullSizePitch)
                .post("/api/full_size_pitches")
                .then()
                .statusCode(201)
                .extract().as(FullSizePitch.class);
        assertThat(saved.getId()).isNotNull();
        clearFullSizePitchAfterTest(saved.getId());
    }

    @Test
    public void postFailNoName() {
        FullSizePitch fullSizePitch = createFullSizePitch();
        fullSizePitch.setName(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(fullSizePitch)
                .post("/api/full_size_pitches")
                .then()
                .statusCode(400);
    }

    @Test
    public void put() {
        FullSizePitch fullSizePitch = createFullSizePitch();
        FullSizePitch saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(fullSizePitch)
                .post("/api/full_size_pitches")
                .then()
                .statusCode(201)
                .extract().as(FullSizePitch.class);
        saved.setName("Updated");
        FullSizePitch updated = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/full_size_pitches")
                .then()
                .statusCode(200)
                .extract().as(FullSizePitch.class);
        assertThat(updated.getName()).isEqualTo("Updated");
        clearFullSizePitchAfterTest(saved.getId());

    }

    @Test
    public void putIsFullRentedNull() {
        FullSizePitch fullSizePitch = createFullSizePitch();
        FullSizePitch saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(fullSizePitch)
                .post("/api/full_size_pitches")
                .then()
                .statusCode(201)
                .extract().as(FullSizePitch.class);
        saved.setIsHalfRentable(null);
        FullSizePitch updated = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/full_size_pitches")
                .then()
                .statusCode(200)
                .extract().as(FullSizePitch.class);
        assertThat(updated.getIsHalfRentable()).isEqualTo(null);
        clearFullSizePitchAfterTest(saved.getId());

    }

    @Test
    public void putFailNoFullPrice() {
        FullSizePitch fullSizePitch = createFullSizePitch();
        FullSizePitch saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(fullSizePitch)
                .post("/api/full_size_pitches")
                .then()
                .statusCode(201)
                .extract().as(FullSizePitch.class);
        saved.setFullPrice(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/full_size_pitches")
                .then()
                .statusCode(400);
        clearFullSizePitchAfterTest(saved.getId());
    }

    @Test
    public void postFailDuplicateName() {
        FullSizePitch fullSizePitch = TestUtils.createFullSizePitch();
        FullSizePitch saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(fullSizePitch)
                .post("/api/full_size_pitches")
                .then()
                .extract().as(FullSizePitch.class);

        FullSizePitch fullSizePitch2 = TestUtils.createFullSizePitch();
        fullSizePitch2.setName(fullSizePitch.getName());

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(fullSizePitch2)
                .post("/api/full_size_pitches")
                .then()
                .statusCode(403)
                .extract().response();
        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(403);
        assertThat(actualExceptionMessage).isEqualTo("Sport object with given name already exists");

        clearFullSizePitchAfterTest(saved.getId());
    }

    @Test
    public void putFailEmptyId() {
        FullSizePitch fullSizePitch = TestUtils.createFullSizePitch();
        FullSizePitch saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(fullSizePitch)
                .post("/api/full_size_pitches")
                .then()
                .statusCode(201)
                .extract().as(FullSizePitch.class);
        int id = saved.getId(); // we will need that to delete after test.
        saved.setId(null);
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/full_size_pitches")
                .then()
                .statusCode(400)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(actualExceptionMessage).isEqualTo("Id for updating full size pitch cannot be null");

        clearFullSizePitchAfterTest(id);
    }

    @Test
    public void putFailNotFound() {
        FullSizePitch fullSizePitch = TestUtils.createFullSizePitch();
        //there will never be id 0 in repo.
        fullSizePitch.setId(0);
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(fullSizePitch)
                .put("/api/full_size_pitches")
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Full size pitch for given id not found. Try to search in all sport objects or change the id.");
    }

    @Test
    public void putEquipmentToFullSizePitch() {
        FullSizePitch fullSizePitch = TestUtils.createFullSizePitch();
        RentEquipment rentEquipment = TestUtils.createRentEquipment();

        FullSizePitch savedTrack = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(fullSizePitch)
                .post("/api/full_size_pitches")
                .then()
                .statusCode(201)
                .extract().as(FullSizePitch.class);

        RentEquipment savedEquipment = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(rentEquipment)
                .post("/api/rent_equipments")
                .then()
                .statusCode(201)
                .extract().as(RentEquipment.class);

        FullSizePitch trackAfterPut = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(fullSizePitch)
                .put("/api/full_size_pitches/{fullSizePitchId}/rent_equipment/{rentEquipmentId}",
                        savedTrack.getId(), savedEquipment.getId())
                .then()
                .statusCode(200)
                .extract().as(FullSizePitch.class);

        //compare name of rent equipment
        assertThat(new ArrayList<>(trackAfterPut.getRentEquipmentNames()).get(0)).isEqualTo(savedEquipment.getName());

        clearRentEquipmentAfterTest(savedEquipment.getId());
        clearFullSizePitchAfterTest(savedTrack.getId());
    }

    @Test
    public void putEquipmentToFullSizePitchFullSizePitchNotFound() {
        FullSizePitch fullSizePitch = TestUtils.createFullSizePitch();
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
                .body(fullSizePitch)
                .put("/api/full_size_pitches/0/rent_equipment/{rentEquipmentId}",
                        savedEquipment.getId())
                .then()
                .statusCode(404)
                .extract().response();

        //compare name of rent equipment
//        assertThat(new ArrayList<>(trackAfterPut.getRentEquipmentNames()).get(0)).isEqualTo(savedEquipment.getName());

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Full size pitch for given id not found. Try to search in all sport objects or change the id.");


        clearRentEquipmentAfterTest(savedEquipment.getId());
//        clearFullSizePitchAfterTest(savedTrack.getId());
    }

    @Test
    public void putEquipmentToFullSizePitchRentEquipmentNotFound() {
        FullSizePitch fullSizePitch = TestUtils.createFullSizePitch();

        FullSizePitch savedTrack = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(fullSizePitch)
                .post("/api/full_size_pitches")
                .then()
                .statusCode(201)
                .extract().as(FullSizePitch.class);

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(fullSizePitch)
                .put("/api/full_size_pitches/{fullSizePitchId}/rent_equipment/0",
                        savedTrack.getId())
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Rent equipment for given id not found!");

        clearFullSizePitchAfterTest(savedTrack.getId());
    }


    private void clearFullSizePitchAfterTest(Integer id) {
        sportObjectService.deleteSportObjectById(id);
    }

    private void clearRentEquipmentAfterTest(Integer id) {
        rentEquipmentService.deleteRentEquipmentById(id);
    }
}
