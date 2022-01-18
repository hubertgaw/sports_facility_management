package pl.lodz.hubertgaw;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.lodz.hubertgaw.dto.SportSwimmingPool;
import pl.lodz.hubertgaw.dto.RentEquipment;
import pl.lodz.hubertgaw.dto.SportSwimmingPool;
import pl.lodz.hubertgaw.service.RentEquipmentService;
import pl.lodz.hubertgaw.service.SportObjectService;
import pl.lodz.hubertgaw.utils.TestUtils;

import javax.inject.Inject;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static pl.lodz.hubertgaw.utils.TestUtils.createSportSwimmingPool;

@QuarkusTest
public class SportSwimmingPoolResourceTest {

    @Inject
    SportObjectService sportObjectService;
    @Inject
    RentEquipmentService rentEquipmentService;

    @Test
    public void getAll() {
        given()
                .when().get("/api/sport_swimming_pools")
                .then()
                .statusCode(200);
    }

    @Test
    public void getById() {
        SportSwimmingPool sportSwimmingPool = createSportSwimmingPool();
        SportSwimmingPool saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportSwimmingPool)
                .post("/api/sport_swimming_pools")
                .then()
                .statusCode(201)
                .extract().as(SportSwimmingPool.class);
        SportSwimmingPool got = given()
                .when().get("/api/sport_swimming_pools/{sportSwimmingPoolId}", saved.getId())
                .then()
                .statusCode(200)
                .extract().as(SportSwimmingPool.class);
        assertThat(saved).isEqualTo(got);
        clearSportSwimmingPoolAfterTest(saved.getId());
//        assertThat(saved.equals(got)).isTrue();
    }

    @Test
    public void post() {
        SportSwimmingPool sportSwimmingPool = createSportSwimmingPool();
        SportSwimmingPool saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportSwimmingPool)
                .post("/api/sport_swimming_pools")
                .then()
                .statusCode(201)
                .extract().as(SportSwimmingPool.class);
        assertThat(saved.getId()).isNotNull();
        clearSportSwimmingPoolAfterTest(saved.getId());
    }

    @Test
    public void postFailNoName() {
        SportSwimmingPool sportSwimmingPool = createSportSwimmingPool();
        sportSwimmingPool.setName(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportSwimmingPool)
                .post("/api/sport_swimming_pools")
                .then()
                .statusCode(400);
    }

    @Test
    public void put() {
        SportSwimmingPool sportSwimmingPool = createSportSwimmingPool();
        SportSwimmingPool saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportSwimmingPool)
                .post("/api/sport_swimming_pools")
                .then()
                .statusCode(201)
                .extract().as(SportSwimmingPool.class);
        saved.setName("Updated");
        SportSwimmingPool updated = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/sport_swimming_pools")
                .then()
                .statusCode(200)
                .extract().as(SportSwimmingPool.class);
        assertThat(updated.getName()).isEqualTo("Updated");
        clearSportSwimmingPoolAfterTest(saved.getId());

    }

    @Test
    public void putFailNoTracksNumber() {
        SportSwimmingPool sportSwimmingPool = createSportSwimmingPool();
        SportSwimmingPool saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportSwimmingPool)
                .post("/api/sport_swimming_pools")
                .then()
                .statusCode(201)
                .extract().as(SportSwimmingPool.class);
        saved.setTracksNumber(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/sport_swimming_pools")
                .then()
                .statusCode(400);
        clearSportSwimmingPoolAfterTest(saved.getId());
    }

    @Test
    public void postFailDuplicateName() {
        SportSwimmingPool sportSwimmingPool = TestUtils.createSportSwimmingPool();
        SportSwimmingPool saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportSwimmingPool)
                .post("/api/sport_swimming_pools")
                .then()
                .extract().as(SportSwimmingPool.class);

        SportSwimmingPool sportSwimmingPool2 = TestUtils.createSportSwimmingPool();
        sportSwimmingPool2.setName(sportSwimmingPool.getName());

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportSwimmingPool2)
                .post("/api/sport_swimming_pools")
                .then()
                .statusCode(403)
                .extract().response();
        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(403);
        assertThat(actualExceptionMessage).isEqualTo("Sport object with given name already exists");

        clearSportSwimmingPoolAfterTest(saved.getId());
    }

    @Test
    public void putFailEmptyId() {
        SportSwimmingPool sportSwimmingPool = TestUtils.createSportSwimmingPool();
        SportSwimmingPool saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportSwimmingPool)
                .post("/api/sport_swimming_pools")
                .then()
                .statusCode(201)
                .extract().as(SportSwimmingPool.class);
        int id = saved.getId(); // we will need that to delete after test.
        saved.setId(null);
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/sport_swimming_pools")
                .then()
                .statusCode(400)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(actualExceptionMessage).isEqualTo("Id for updating sport swimming pool cannot be null");

        clearSportSwimmingPoolAfterTest(id);
    }

    @Test
    public void putFailNotFound() {
        SportSwimmingPool sportSwimmingPool = TestUtils.createSportSwimmingPool();
        //there will never be id 0 in repo.
        sportSwimmingPool.setId(0);
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportSwimmingPool)
                .put("/api/sport_swimming_pools")
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Sport swimming pool for given id not found. Try to search in all sport objects or change the id.");
    }

    @Test
    public void putEquipmentToSportSwimmingPool() {
        SportSwimmingPool sportSwimmingPool = TestUtils.createSportSwimmingPool();
        RentEquipment rentEquipment = TestUtils.createRentEquipment();

        SportSwimmingPool savedTrack = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportSwimmingPool)
                .post("/api/sport_swimming_pools")
                .then()
                .statusCode(201)
                .extract().as(SportSwimmingPool.class);

        RentEquipment savedEquipment = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(rentEquipment)
                .post("/api/rent_equipments")
                .then()
                .statusCode(201)
                .extract().as(RentEquipment.class);

        SportSwimmingPool trackAfterPut = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportSwimmingPool)
                .put("/api/sport_swimming_pools/{sportSwimmingPoolId}/rent_equipment/{rentEquipmentId}",
                        savedTrack.getId(), savedEquipment.getId())
                .then()
                .statusCode(200)
                .extract().as(SportSwimmingPool.class);

        //compare name of rent equipment
        assertThat(new ArrayList<>(trackAfterPut.getRentEquipmentNames()).get(0)).isEqualTo(savedEquipment.getName());

        clearRentEquipmentAfterTest(savedEquipment.getId());
        clearSportSwimmingPoolAfterTest(savedTrack.getId());
    }

    @Test
    public void putEquipmentToSportSwimmingPoolSportSwimmingPoolNotFound() {
        SportSwimmingPool sportSwimmingPool = TestUtils.createSportSwimmingPool();
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
                .body(sportSwimmingPool)
                .put("/api/sport_swimming_pools/0/rent_equipment/{rentEquipmentId}",
                        savedEquipment.getId())
                .then()
                .statusCode(404)
                .extract().response();

        //compare name of rent equipment
//        assertThat(new ArrayList<>(trackAfterPut.getRentEquipmentNames()).get(0)).isEqualTo(savedEquipment.getName());

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Sport swimming pool for given id not found. Try to search in all sport objects or change the id.");


        clearRentEquipmentAfterTest(savedEquipment.getId());
//        clearSportSwimmingPoolAfterTest(savedTrack.getId());
    }

    @Test
    public void putEquipmentToSportSwimmingPoolRentEquipmentNotFound() {
        SportSwimmingPool sportSwimmingPool = TestUtils.createSportSwimmingPool();

        SportSwimmingPool savedTrack = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportSwimmingPool)
                .post("/api/sport_swimming_pools")
                .then()
                .statusCode(201)
                .extract().as(SportSwimmingPool.class);

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportSwimmingPool)
                .put("/api/sport_swimming_pools/{sportSwimmingPoolId}/rent_equipment/0",
                        savedTrack.getId())
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Rent equipment for given id not found!");

        clearSportSwimmingPoolAfterTest(savedTrack.getId());
    }

    private void clearSportSwimmingPoolAfterTest(Integer id) {
        sportObjectService.deleteSportObjectById(id);
    }

    private void clearRentEquipmentAfterTest(Integer id) {
        rentEquipmentService.deleteRentEquipmentById(id);
    }
}
