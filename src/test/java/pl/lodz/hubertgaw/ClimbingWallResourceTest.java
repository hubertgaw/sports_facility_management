package pl.lodz.hubertgaw;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.lodz.hubertgaw.dto.ClimbingWall;
import pl.lodz.hubertgaw.dto.ClimbingWall;
import pl.lodz.hubertgaw.dto.RentEquipment;
import pl.lodz.hubertgaw.service.RentEquipmentService;
import pl.lodz.hubertgaw.service.SportObjectService;
import pl.lodz.hubertgaw.utils.TestUtils;

import javax.inject.Inject;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static pl.lodz.hubertgaw.utils.TestUtils.createClimbingWall;

@QuarkusTest
@TestSecurity(authorizationEnabled = false)
public class ClimbingWallResourceTest {

    @Test
    public void getAll() {
        given()
                .when().get("/api/climbing_walls")
                .then()
                .statusCode(200);
    }

    @Test
    public void getById() {
        ClimbingWall climbingWall = createClimbingWall();
        ClimbingWall saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(climbingWall)
                .post("/api/climbing_walls")
                .then()
                .statusCode(201)
                .extract().as(ClimbingWall.class);
        ClimbingWall got = given()
                .when().get("/api/climbing_walls/{climbingWallId}", saved.getId())
                .then()
                .statusCode(200)
                .extract().as(ClimbingWall.class);
        assertThat(saved).isEqualTo(got);
        TestUtils.clearSportObjectAfterTest(saved.getId());
//        assertThat(saved.equals(got)).isTrue();
    }

    @Test
    public void getByIdFailNotFound() {
        Response response = given()
                .when().get("/api/climbing_walls/{climbingWallId}", 0)// there will be for sure no object with id 0
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Climbing wall for given id not found. Try to search in all sport objects or change the id.");

    }


    @Test
    public void post() {
        ClimbingWall climbingWall = createClimbingWall();
        ClimbingWall saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(climbingWall)
                .post("/api/climbing_walls")
                .then()
                .statusCode(201)
                .extract().as(ClimbingWall.class);
        assertThat(saved.getId()).isNotNull();
        TestUtils.clearSportObjectAfterTest(saved.getId());
    }

    @Test
    public void postFailNoName() {
        ClimbingWall climbingWall = createClimbingWall();
        climbingWall.setName(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(climbingWall)
                .post("/api/climbing_walls")
                .then()
                .statusCode(400);
    }

    @Test
    public void put() {
        ClimbingWall climbingWall = createClimbingWall();
        ClimbingWall saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(climbingWall)
                .post("/api/climbing_walls")
                .then()
                .statusCode(201)
                .extract().as(ClimbingWall.class);
        saved.setName("Updated");
        ClimbingWall updated = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/climbing_walls")
                .then()
                .statusCode(200)
                .extract().as(ClimbingWall.class);
        assertThat(updated.getName()).isEqualTo("Updated");
        TestUtils.clearSportObjectAfterTest(saved.getId());

    }

    @Test
    public void putFailNoCapacity() {
        ClimbingWall climbingWall = createClimbingWall();
        ClimbingWall saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(climbingWall)
                .post("/api/climbing_walls")
                .then()
                .statusCode(201)
                .extract().as(ClimbingWall.class);
        saved.setCapacity(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/climbing_walls")
                .then()
                .statusCode(400);
        TestUtils.clearSportObjectAfterTest(saved.getId());
    }

    @Test
    public void postFailDuplicateName() {
        ClimbingWall climbingWall = TestUtils.createClimbingWall();
        ClimbingWall saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(climbingWall)
                .post("/api/climbing_walls")
                .then()
                .extract().as(ClimbingWall.class);

        ClimbingWall climbingWall2 = TestUtils.createClimbingWall();
        climbingWall2.setName(climbingWall.getName());

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(climbingWall2)
                .post("/api/climbing_walls")
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
        ClimbingWall climbingWall = TestUtils.createClimbingWall();
        ClimbingWall saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(climbingWall)
                .post("/api/climbing_walls")
                .then()
                .statusCode(201)
                .extract().as(ClimbingWall.class);
        int id = saved.getId(); // we will need that to delete after test.
        saved.setId(null);
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/climbing_walls")
                .then()
                .statusCode(400)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(actualExceptionMessage).isEqualTo("Id for updating climbing wall cannot be null");

        TestUtils.clearSportObjectAfterTest(id);
    }

    @Test
    public void putFailNotFound() {
        ClimbingWall climbingWall = TestUtils.createClimbingWall();
        //there will never be id 0 in repo.
        climbingWall.setId(0);
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(climbingWall)
                .put("/api/climbing_walls")
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Climbing wall for given id not found. Try to search in all sport objects or change the id.");
    }

    @Test
    public void putEquipmentToClimbingWall() {
        ClimbingWall climbingWall = TestUtils.createClimbingWall();
        RentEquipment rentEquipment = TestUtils.createRentEquipment();

        ClimbingWall savedTrack = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(climbingWall)
                .post("/api/climbing_walls")
                .then()
                .statusCode(201)
                .extract().as(ClimbingWall.class);

        RentEquipment savedEquipment = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(rentEquipment)
                .post("/api/rent_equipments")
                .then()
                .statusCode(201)
                .extract().as(RentEquipment.class);

        ClimbingWall trackAfterPut = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(climbingWall)
                .put("/api/climbing_walls/{climbingWallId}/rent_equipment/{rentEquipmentId}",
                        savedTrack.getId(), savedEquipment.getId())
                .then()
                .statusCode(200)
                .extract().as(ClimbingWall.class);

        //compare name of rent equipment
        assertThat(new ArrayList<>(trackAfterPut.getRentEquipmentNames()).get(0)).isEqualTo(savedEquipment.getName());

        TestUtils.clearRentEquipmentAfterTest(savedEquipment.getId());
        TestUtils.clearSportObjectAfterTest(savedTrack.getId());
    }

    @Test
    public void putEquipmentToClimbingWallClimbingWallNotFound() {
        ClimbingWall climbingWall = TestUtils.createClimbingWall();
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
                .body(climbingWall)
                .put("/api/climbing_walls/0/rent_equipment/{rentEquipmentId}",
                        savedEquipment.getId())
                .then()
                .statusCode(404)
                .extract().response();

        //compare name of rent equipment
//        assertThat(new ArrayList<>(trackAfterPut.getRentEquipmentNames()).get(0)).isEqualTo(savedEquipment.getName());

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Climbing wall for given id not found. Try to search in all sport objects or change the id.");


        TestUtils.clearRentEquipmentAfterTest(savedEquipment.getId());
//        TestUtils.clearSportObjectAfterTest(savedTrack.getId());
    }

    @Test
    public void putEquipmentToClimbingWallRentEquipmentNotFound() {
        ClimbingWall climbingWall = TestUtils.createClimbingWall();

        ClimbingWall savedTrack = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(climbingWall)
                .post("/api/climbing_walls")
                .then()
                .statusCode(201)
                .extract().as(ClimbingWall.class);

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(climbingWall)
                .put("/api/climbing_walls/{climbingWallId}/rent_equipment/0",
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
