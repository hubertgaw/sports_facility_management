package pl.lodz.hubertgaw;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.lodz.hubertgaw.dto.Gym;
import pl.lodz.hubertgaw.dto.Gym;
import pl.lodz.hubertgaw.dto.RentEquipment;
import pl.lodz.hubertgaw.service.RentEquipmentService;
import pl.lodz.hubertgaw.service.SportObjectService;
import pl.lodz.hubertgaw.utils.TestUtils;

import javax.inject.Inject;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static pl.lodz.hubertgaw.utils.TestUtils.createGym;

@QuarkusTest
public class GymResourceTest {

    @Inject
    SportObjectService sportObjectService;
    @Inject
    RentEquipmentService rentEquipmentService;

    @Test
    public void getAll() {
        given()
                .when().get("/api/gyms")
                .then()
                .statusCode(200);
    }

    @Test
    public void getById() {
        Gym gym = createGym();
        Gym saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(gym)
                .post("/api/gyms")
                .then()
                .statusCode(201)
                .extract().as(Gym.class);
        Gym got = given()
                .when().get("/api/gyms/{gymId}", saved.getId())
                .then()
                .statusCode(200)
                .extract().as(Gym.class);
        assertThat(saved).isEqualTo(got);
        clearGymAfterTest(saved.getId());
//        assertThat(saved.equals(got)).isTrue();
    }

    @Test
    public void post() {
        Gym gym = createGym();
        Gym saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(gym)
                .post("/api/gyms")
                .then()
                .statusCode(201)
                .extract().as(Gym.class);
        assertThat(saved.getId()).isNotNull();
        clearGymAfterTest(saved.getId());
    }

    @Test
    public void postFailNoName() {
        Gym gym = createGym();
        gym.setName(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(gym)
                .post("/api/gyms")
                .then()
                .statusCode(400);
    }

    @Test
    public void put() {
        Gym gym = createGym();
        Gym saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(gym)
                .post("/api/gyms")
                .then()
                .statusCode(201)
                .extract().as(Gym.class);
        saved.setName("Updated");
        Gym updated = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/gyms")
                .then()
                .statusCode(200)
                .extract().as(Gym.class);
        assertThat(updated.getName()).isEqualTo("Updated");
        clearGymAfterTest(saved.getId());

    }

    @Test
    public void putFailNoCapacity() {
        Gym gym = createGym();
        Gym saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(gym)
                .post("/api/gyms")
                .then()
                .statusCode(201)
                .extract().as(Gym.class);
        saved.setCapacity(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/gyms")
                .then()
                .statusCode(400);
        clearGymAfterTest(saved.getId());
    }

    @Test
    public void postFailDuplicateName() {
        Gym gym = TestUtils.createGym();
        Gym saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(gym)
                .post("/api/gyms")
                .then()
                .extract().as(Gym.class);

        Gym gym2 = TestUtils.createGym();
        gym2.setName(gym.getName());

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(gym2)
                .post("/api/gyms")
                .then()
                .statusCode(403)
                .extract().response();
        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(403);
        assertThat(actualExceptionMessage).isEqualTo("Sport object with given name already exists");

        clearGymAfterTest(saved.getId());
    }

    @Test
    public void putFailEmptyId() {
        Gym gym = TestUtils.createGym();
        Gym saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(gym)
                .post("/api/gyms")
                .then()
                .statusCode(201)
                .extract().as(Gym.class);
        int id = saved.getId(); // we will need that to delete after test.
        saved.setId(null);
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/gyms")
                .then()
                .statusCode(400)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(actualExceptionMessage).isEqualTo("Id for updating gym cannot be null");

        clearGymAfterTest(id);
    }

    @Test
    public void putFailNotFound() {
        Gym gym = TestUtils.createGym();
        //there will never be id 0 in repo.
        gym.setId(0);
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(gym)
                .put("/api/gyms")
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Gym for given id not found. Try to search in all sport objects or change the id.");
    }

    @Test
    public void putEquipmentToGym() {
        Gym gym = TestUtils.createGym();
        RentEquipment rentEquipment = TestUtils.createRentEquipment();

        Gym savedTrack = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(gym)
                .post("/api/gyms")
                .then()
                .statusCode(201)
                .extract().as(Gym.class);

        RentEquipment savedEquipment = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(rentEquipment)
                .post("/api/rent_equipments")
                .then()
                .statusCode(201)
                .extract().as(RentEquipment.class);

        Gym trackAfterPut = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(gym)
                .put("/api/gyms/{gymId}/rent_equipment/{rentEquipmentId}",
                        savedTrack.getId(), savedEquipment.getId())
                .then()
                .statusCode(200)
                .extract().as(Gym.class);

        //compare name of rent equipment
        assertThat(new ArrayList<>(trackAfterPut.getRentEquipmentNames()).get(0)).isEqualTo(savedEquipment.getName());

        clearRentEquipmentAfterTest(savedEquipment.getId());
        clearGymAfterTest(savedTrack.getId());
    }

    @Test
    public void putEquipmentToGymGymNotFound() {
        Gym gym = TestUtils.createGym();
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
                .body(gym)
                .put("/api/gyms/0/rent_equipment/{rentEquipmentId}",
                        savedEquipment.getId())
                .then()
                .statusCode(404)
                .extract().response();

        //compare name of rent equipment
//        assertThat(new ArrayList<>(trackAfterPut.getRentEquipmentNames()).get(0)).isEqualTo(savedEquipment.getName());

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Gym for given id not found. Try to search in all sport objects or change the id.");


        clearRentEquipmentAfterTest(savedEquipment.getId());
//        clearGymAfterTest(savedTrack.getId());
    }

    @Test
    public void putEquipmentToGymRentEquipmentNotFound() {
        Gym gym = TestUtils.createGym();

        Gym savedTrack = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(gym)
                .post("/api/gyms")
                .then()
                .statusCode(201)
                .extract().as(Gym.class);

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(gym)
                .put("/api/gyms/{gymId}/rent_equipment/0",
                        savedTrack.getId())
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Rent equipment for given id not found!");

        clearGymAfterTest(savedTrack.getId());
    }

    private void clearGymAfterTest(Integer id) {
        sportObjectService.deleteSportObjectById(id);
    }

    private void clearRentEquipmentAfterTest(Integer id) {
        rentEquipmentService.deleteRentEquipmentById(id);
    }
}
