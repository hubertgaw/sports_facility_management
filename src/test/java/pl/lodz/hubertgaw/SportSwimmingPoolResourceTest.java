package pl.lodz.hubertgaw;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import pl.lodz.hubertgaw.dto.RentEquipment;
import pl.lodz.hubertgaw.dto.SportSwimmingPool;
import pl.lodz.hubertgaw.utils.TestUtils;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static pl.lodz.hubertgaw.utils.TestUtils.createSportSwimmingPool;

@QuarkusTest
@TestSecurity(authorizationEnabled = false)
public class SportSwimmingPoolResourceTest {

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
        TestUtils.clearSportObjectAfterTest(saved.getId());
//        assertThat(saved.equals(got)).isTrue();
    }

    @Test
    public void getByIdFailNotFound() {
        Response response = given()
                .when().get("/api/sport_swimming_pools/{sportSwimmingPoolId}", 0)// there will be for sure no object with id 0
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Sport swimming pool for given id not found. Try to search in all sport objects or change the id.");

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
        TestUtils.clearSportObjectAfterTest(saved.getId());
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
        TestUtils.clearSportObjectAfterTest(saved.getId());

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
        TestUtils.clearSportObjectAfterTest(saved.getId());
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

        TestUtils.clearSportObjectAfterTest(saved.getId());
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

        TestUtils.clearSportObjectAfterTest(id);
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

        TestUtils.clearRentEquipmentAfterTest(savedEquipment.getId());
        TestUtils.clearSportObjectAfterTest(savedTrack.getId());
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


        TestUtils.clearRentEquipmentAfterTest(savedEquipment.getId());
//        TestUtils.clearSportObjectAfterTest(savedTrack.getId());
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

        TestUtils.clearSportObjectAfterTest(savedTrack.getId());
    }

}
