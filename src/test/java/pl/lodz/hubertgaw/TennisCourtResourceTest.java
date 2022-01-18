package pl.lodz.hubertgaw;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.lodz.hubertgaw.dto.TennisCourt;
import pl.lodz.hubertgaw.dto.RentEquipment;
import pl.lodz.hubertgaw.dto.TennisCourt;
import pl.lodz.hubertgaw.service.RentEquipmentService;
import pl.lodz.hubertgaw.service.SportObjectService;
import pl.lodz.hubertgaw.utils.TestUtils;

import javax.inject.Inject;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static pl.lodz.hubertgaw.utils.TestUtils.createTennisCourt;

@QuarkusTest
public class TennisCourtResourceTest {

    @Inject
    SportObjectService sportObjectService;
    @Inject
    RentEquipmentService rentEquipmentService;

    @Test
    public void getAll() {
        given()
                .when().get("/api/tennis_courts")
                .then()
                .statusCode(200);
    }

    @Test
    public void getById() {
        TennisCourt tennisCourt = createTennisCourt();
        TennisCourt saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tennisCourt)
                .post("/api/tennis_courts")
                .then()
                .statusCode(201)
                .extract().as(TennisCourt.class);
        TennisCourt got = given()
                .when().get("/api/tennis_courts/{tennisCourtId}", saved.getId())
                .then()
                .statusCode(200)
                .extract().as(TennisCourt.class);
        assertThat(saved).isEqualTo(got);
        clearTennisCourtAfterTest(saved.getId());
//        assertThat(saved.equals(got)).isTrue();
    }

    @Test
    public void post() {
        TennisCourt tennisCourt = createTennisCourt();
        TennisCourt saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tennisCourt)
                .post("/api/tennis_courts")
                .then()
                .statusCode(201)
                .extract().as(TennisCourt.class);
        assertThat(saved.getId()).isNotNull();
        clearTennisCourtAfterTest(saved.getId());
    }

    @Test
    public void postFailNoName() {
        TennisCourt tennisCourt = createTennisCourt();
        tennisCourt.setName(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tennisCourt)
                .post("/api/tennis_courts")
                .then()
                .statusCode(400);
    }

    @Test
    public void put() {
        TennisCourt tennisCourt = createTennisCourt();
        TennisCourt saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tennisCourt)
                .post("/api/tennis_courts")
                .then()
                .statusCode(201)
                .extract().as(TennisCourt.class);
        saved.setName("Updated");
        TennisCourt updated = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/tennis_courts")
                .then()
                .statusCode(200)
                .extract().as(TennisCourt.class);
        assertThat(updated.getName()).isEqualTo("Updated");
        clearTennisCourtAfterTest(saved.getId());

    }

    @Test
    public void putFailNoFullPrice() {
        TennisCourt tennisCourt = createTennisCourt();
        TennisCourt saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tennisCourt)
                .post("/api/tennis_courts")
                .then()
                .statusCode(201)
                .extract().as(TennisCourt.class);
        saved.setFullPrice(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/tennis_courts")
                .then()
                .statusCode(400);
        clearTennisCourtAfterTest(saved.getId());
    }

    @Test
    public void postFailDuplicateName() {
        TennisCourt tennisCourt = TestUtils.createTennisCourt();
        TennisCourt saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tennisCourt)
                .post("/api/tennis_courts")
                .then()
                .extract().as(TennisCourt.class);

        TennisCourt tennisCourt2 = TestUtils.createTennisCourt();
        tennisCourt2.setName(tennisCourt.getName());

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tennisCourt2)
                .post("/api/tennis_courts")
                .then()
                .statusCode(403)
                .extract().response();
        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(403);
        assertThat(actualExceptionMessage).isEqualTo("Sport object with given name already exists");

        clearTennisCourtAfterTest(saved.getId());
    }

    @Test
    public void putFailEmptyId() {
        TennisCourt tennisCourt = TestUtils.createTennisCourt();
        TennisCourt saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tennisCourt)
                .post("/api/tennis_courts")
                .then()
                .statusCode(201)
                .extract().as(TennisCourt.class);
        int id = saved.getId(); // we will need that to delete after test.
        saved.setId(null);
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/tennis_courts")
                .then()
                .statusCode(400)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(actualExceptionMessage).isEqualTo("Id for updating tennis court cannot be null");

        clearTennisCourtAfterTest(id);
    }

    @Test
    public void putFailNotFound() {
        TennisCourt tennisCourt = TestUtils.createTennisCourt();
        //there will never be id 0 in repo.
        tennisCourt.setId(0);
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tennisCourt)
                .put("/api/tennis_courts")
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Tennis court for given id not found. Try to search in all sport objects or change the id.");
    }

    @Test
    public void putEquipmentToTennisCourt() {
        TennisCourt tennisCourt = TestUtils.createTennisCourt();
        RentEquipment rentEquipment = TestUtils.createRentEquipment();

        TennisCourt savedTrack = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tennisCourt)
                .post("/api/tennis_courts")
                .then()
                .statusCode(201)
                .extract().as(TennisCourt.class);

        RentEquipment savedEquipment = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(rentEquipment)
                .post("/api/rent_equipments")
                .then()
                .statusCode(201)
                .extract().as(RentEquipment.class);

        TennisCourt trackAfterPut = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tennisCourt)
                .put("/api/tennis_courts/{tennisCourtId}/rent_equipment/{rentEquipmentId}",
                        savedTrack.getId(), savedEquipment.getId())
                .then()
                .statusCode(200)
                .extract().as(TennisCourt.class);

        //compare name of rent equipment
        assertThat(new ArrayList<>(trackAfterPut.getRentEquipmentNames()).get(0)).isEqualTo(savedEquipment.getName());

        clearRentEquipmentAfterTest(savedEquipment.getId());
        clearTennisCourtAfterTest(savedTrack.getId());
    }

    @Test
    public void putEquipmentToTennisCourtTennisCourtNotFound() {
        TennisCourt tennisCourt = TestUtils.createTennisCourt();
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
                .body(tennisCourt)
                .put("/api/tennis_courts/0/rent_equipment/{rentEquipmentId}",
                        savedEquipment.getId())
                .then()
                .statusCode(404)
                .extract().response();

        //compare name of rent equipment
//        assertThat(new ArrayList<>(trackAfterPut.getRentEquipmentNames()).get(0)).isEqualTo(savedEquipment.getName());

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Tennis court for given id not found. Try to search in all sport objects or change the id.");


        clearRentEquipmentAfterTest(savedEquipment.getId());
//        clearTennisCourtAfterTest(savedTrack.getId());
    }

    @Test
    public void putEquipmentToTennisCourtRentEquipmentNotFound() {
        TennisCourt tennisCourt = TestUtils.createTennisCourt();

        TennisCourt savedTrack = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tennisCourt)
                .post("/api/tennis_courts")
                .then()
                .statusCode(201)
                .extract().as(TennisCourt.class);

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tennisCourt)
                .put("/api/tennis_courts/{tennisCourtId}/rent_equipment/0",
                        savedTrack.getId())
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Rent equipment for given id not found!");

        clearTennisCourtAfterTest(savedTrack.getId());
    }


    private void clearTennisCourtAfterTest(Integer id) {
        sportObjectService.deleteSportObjectById(id);
    }

    private void clearRentEquipmentAfterTest(Integer id) {
        rentEquipmentService.deleteRentEquipmentById(id);
    }
}
