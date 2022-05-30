package pl.lodz.hubertgaw;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.lodz.hubertgaw.dto.SportsHall;
import pl.lodz.hubertgaw.dto.RentEquipment;
import pl.lodz.hubertgaw.dto.SportsHall;
import pl.lodz.hubertgaw.service.RentEquipmentService;
import pl.lodz.hubertgaw.service.SportObjectService;
import pl.lodz.hubertgaw.utils.TestUtils;

import javax.inject.Inject;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static pl.lodz.hubertgaw.utils.TestUtils.createSportsHall;

@QuarkusTest
@TestSecurity(authorizationEnabled = false)
public class SportsHallResourceTest {

    @Test
    public void getAll() {
        given()
                .when().get("/api/sports_halls")
                .then()
                .statusCode(200);
    }

    @Test
    public void getById() {
        SportsHall sportsHall = createSportsHall();
        SportsHall saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportsHall)
                .post("/api/sports_halls")
                .then()
                .statusCode(201)
                .extract().as(SportsHall.class);
        SportsHall got = given()
                .when().get("/api/sports_halls/{sportsHallId}", saved.getId())
                .then()
                .statusCode(200)
                .extract().as(SportsHall.class);
        assertThat(saved).isEqualTo(got);
        TestUtils.clearSportObjectAfterTest(saved.getId());
//        assertThat(saved.equals(got)).isTrue();
    }

    @Test
    public void getByIdFailNotFound() {
        Response response = given()
                .when().get("/api/sports_halls/{sportsHallId}", 0)// there will be for sure no object with id 0
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Sports hall for given id not found. Try to search in all sport objects or change the id.");

    }

    @Test
    public void post() {
        SportsHall sportsHall = createSportsHall();
        SportsHall saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportsHall)
                .post("/api/sports_halls")
                .then()
                .statusCode(201)
                .extract().as(SportsHall.class);
        assertThat(saved.getId()).isNotNull();
        TestUtils.clearSportObjectAfterTest(saved.getId());
    }

    @Test
    public void postFailNoName() {
        SportsHall sportsHall = createSportsHall();
        sportsHall.setName(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportsHall)
                .post("/api/sports_halls")
                .then()
                .statusCode(400);
    }

    @Test
    public void put() {
        SportsHall sportsHall = createSportsHall();
        SportsHall saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportsHall)
                .post("/api/sports_halls")
                .then()
                .statusCode(201)
                .extract().as(SportsHall.class);
        saved.setName("Updated");
        SportsHall updated = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/sports_halls")
                .then()
                .statusCode(200)
                .extract().as(SportsHall.class);
        assertThat(updated.getName()).isEqualTo("Updated");
        TestUtils.clearSportObjectAfterTest(saved.getId());

    }

    @Test
    public void putFailNoSectorsNumber() {
        SportsHall sportsHall = createSportsHall();
        SportsHall saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportsHall)
                .post("/api/sports_halls")
                .then()
                .statusCode(201)
                .extract().as(SportsHall.class);
        saved.setSectorsNumber(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/sports_halls")
                .then()
                .statusCode(400);
        TestUtils.clearSportObjectAfterTest(saved.getId());
    }

    @Test
    public void postFailDuplicateName() {
        SportsHall sportsHall = TestUtils.createSportsHall();
        SportsHall saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportsHall)
                .post("/api/sports_halls")
                .then()
                .extract().as(SportsHall.class);

        SportsHall sportsHall2 = TestUtils.createSportsHall();
        sportsHall2.setName(sportsHall.getName());

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportsHall2)
                .post("/api/sports_halls")
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
        SportsHall sportsHall = TestUtils.createSportsHall();
        SportsHall saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportsHall)
                .post("/api/sports_halls")
                .then()
                .statusCode(201)
                .extract().as(SportsHall.class);
        int id = saved.getId(); // we will need that to delete after test.
        saved.setId(null);
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/sports_halls")
                .then()
                .statusCode(400)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(actualExceptionMessage).isEqualTo("Id for updating sports hall cannot be null");

        TestUtils.clearSportObjectAfterTest(id);
    }

    @Test
    public void putFailNotFound() {
        SportsHall sportsHall = TestUtils.createSportsHall();
        //there will never be id 0 in repo.
        sportsHall.setId(0);
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportsHall)
                .put("/api/sports_halls")
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Sports hall for given id not found. Try to search in all sport objects or change the id.");
    }

    @Test
    public void putEquipmentToSportsHall() {
        SportsHall sportsHall = TestUtils.createSportsHall();
        RentEquipment rentEquipment = TestUtils.createRentEquipment();

        SportsHall savedTrack = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportsHall)
                .post("/api/sports_halls")
                .then()
                .statusCode(201)
                .extract().as(SportsHall.class);

        RentEquipment savedEquipment = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(rentEquipment)
                .post("/api/rent_equipments")
                .then()
                .statusCode(201)
                .extract().as(RentEquipment.class);

        SportsHall trackAfterPut = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportsHall)
                .put("/api/sports_halls/{sportsHallId}/rent_equipment/{rentEquipmentId}",
                        savedTrack.getId(), savedEquipment.getId())
                .then()
                .statusCode(200)
                .extract().as(SportsHall.class);

        //compare name of rent equipment
        assertThat(new ArrayList<>(trackAfterPut.getRentEquipmentNames()).get(0)).isEqualTo(savedEquipment.getName());

        TestUtils.clearRentEquipmentAfterTest(savedEquipment.getId());
        TestUtils.clearSportObjectAfterTest(savedTrack.getId());
    }

    @Test
    public void putEquipmentToSportsHallSportsHallNotFound() {
        SportsHall sportsHall = TestUtils.createSportsHall();
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
                .body(sportsHall)
                .put("/api/sports_halls/0/rent_equipment/{rentEquipmentId}",
                        savedEquipment.getId())
                .then()
                .statusCode(404)
                .extract().response();

        //compare name of rent equipment
//        assertThat(new ArrayList<>(trackAfterPut.getRentEquipmentNames()).get(0)).isEqualTo(savedEquipment.getName());

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Sports hall for given id not found. Try to search in all sport objects or change the id.");


        TestUtils.clearRentEquipmentAfterTest(savedEquipment.getId());
//        TestUtils.clearSportObjectAfterTest(savedTrack.getId());
    }

    @Test
    public void putEquipmentToSportsHallRentEquipmentNotFound() {
        SportsHall sportsHall = TestUtils.createSportsHall();

        SportsHall savedTrack = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportsHall)
                .post("/api/sports_halls")
                .then()
                .statusCode(201)
                .extract().as(SportsHall.class);

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportsHall)
                .put("/api/sports_halls/{sportsHallId}/rent_equipment/0",
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
