package pl.lodz.hubertgaw;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import pl.lodz.hubertgaw.dto.BeachVolleyballCourt;
import pl.lodz.hubertgaw.dto.RentEquipment;
import pl.lodz.hubertgaw.service.RentEquipmentService;
import pl.lodz.hubertgaw.service.SportObjectService;
import pl.lodz.hubertgaw.utils.TestUtils;

import javax.inject.Inject;
import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static pl.lodz.hubertgaw.utils.TestUtils.createBeachVolleyballCourt;

@QuarkusTest
@TestSecurity(authorizationEnabled = false)
public class BeachVolleyballCourtResourceTest {

    @Inject
    SportObjectService sportObjectService;
    @Inject
    RentEquipmentService rentEquipmentService;

    @Test
    public void getAll() {
        given()
                .when().get("/api/beach_volleyball_courts")
                .then()
                .statusCode(200);
    }

    @Test
    public void getById() {
        BeachVolleyballCourt beachVolleyballCourt = createBeachVolleyballCourt();
        BeachVolleyballCourt saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(beachVolleyballCourt)
                .post("/api/beach_volleyball_courts")
                .then()
                .statusCode(201)
                .extract().as(BeachVolleyballCourt.class);
        BeachVolleyballCourt got = given()
                .when().get("/api/beach_volleyball_courts/{beachVolleyballCourtId}", saved.getId())
                .then()
                .statusCode(200)
                .extract().as(BeachVolleyballCourt.class);
        assertThat(saved).isEqualTo(got);
        TestUtils.clearSportObjectAfterTest(saved.getId());
//        assertThat(saved.equals(got)).isTrue();
    }

    @Test
    public void getByIdFailNotFound() {
        Response response = given()
                .when().get("/api/beach_volleyball_courts/{beachVolleyballCourtId}", 0)// there will be for sure no object with id 0
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Beach volleyball court for given id not found. Try to search in all sport objects or change the id.");

    }


    @Test
    public void post() {
        BeachVolleyballCourt beachVolleyballCourt = createBeachVolleyballCourt();
        BeachVolleyballCourt saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(beachVolleyballCourt)
                .post("/api/beach_volleyball_courts")
                .then()
                .statusCode(201)
                .extract().as(BeachVolleyballCourt.class);
        assertThat(saved.getId()).isNotNull();
        TestUtils.clearSportObjectAfterTest(saved.getId());
    }

    @Test
    public void postFailNoName() {
        BeachVolleyballCourt beachVolleyballCourt = createBeachVolleyballCourt();
        beachVolleyballCourt.setName(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(beachVolleyballCourt)
                .post("/api/beach_volleyball_courts")
                .then()
                .statusCode(400);
    }

    @Test
    public void put() {
        BeachVolleyballCourt beachVolleyballCourt = createBeachVolleyballCourt();
        BeachVolleyballCourt saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(beachVolleyballCourt)
                .post("/api/beach_volleyball_courts")
                .then()
                .statusCode(201)
                .extract().as(BeachVolleyballCourt.class);
        saved.setName("Updated");
        BeachVolleyballCourt updated = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/beach_volleyball_courts")
                .then()
                .statusCode(200)
                .extract().as(BeachVolleyballCourt.class);
        assertThat(updated.getName()).isEqualTo("Updated");
        TestUtils.clearSportObjectAfterTest(saved.getId());

    }

    @Test
    public void putFailNoFullPrice() {
        BeachVolleyballCourt beachVolleyballCourt = createBeachVolleyballCourt();
        BeachVolleyballCourt saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(beachVolleyballCourt)
                .post("/api/beach_volleyball_courts")
                .then()
                .statusCode(201)
                .extract().as(BeachVolleyballCourt.class);
        saved.setFullPrice(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/beach_volleyball_courts")
                .then()
                .statusCode(400);
        TestUtils.clearSportObjectAfterTest(saved.getId());
    }

    @Test
    public void postFailDuplicateName() {
        BeachVolleyballCourt beachVolleyballCourt = TestUtils.createBeachVolleyballCourt();
        BeachVolleyballCourt saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(beachVolleyballCourt)
                .post("/api/beach_volleyball_courts")
                .then()
                .extract().as(BeachVolleyballCourt.class);

        BeachVolleyballCourt beachVolleyballCourt2 = TestUtils.createBeachVolleyballCourt();
        beachVolleyballCourt2.setName(beachVolleyballCourt.getName());

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(beachVolleyballCourt2)
                .post("/api/beach_volleyball_courts")
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
        BeachVolleyballCourt beachVolleyballCourt = TestUtils.createBeachVolleyballCourt();
        BeachVolleyballCourt saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(beachVolleyballCourt)
                .post("/api/beach_volleyball_courts")
                .then()
                .statusCode(201)
                .extract().as(BeachVolleyballCourt.class);
        int id = saved.getId(); // we will need that to delete after test.
        saved.setId(null);
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/beach_volleyball_courts")
                .then()
                .statusCode(400)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(actualExceptionMessage).isEqualTo("Id for updating beach volleyball court cannot be null");

        TestUtils.clearSportObjectAfterTest(id);
    }

    @Test
    public void putFailNotFound() {
        BeachVolleyballCourt beachVolleyballCourt = TestUtils.createBeachVolleyballCourt();
        //there will never be id 0 in repo.
        beachVolleyballCourt.setId(0);
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(beachVolleyballCourt)
                .put("/api/beach_volleyball_courts")
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Beach volleyball court for given id not found. Try to search in all sport objects or change the id.");
    }

    @Test
    public void putEquipmentToBeachVolleyballCourt() {
        BeachVolleyballCourt beachVolleyballCourt = TestUtils.createBeachVolleyballCourt();
        RentEquipment rentEquipment = TestUtils.createRentEquipment();

        BeachVolleyballCourt savedTrack = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(beachVolleyballCourt)
                .post("/api/beach_volleyball_courts")
                .then()
                .statusCode(201)
                .extract().as(BeachVolleyballCourt.class);

        RentEquipment savedEquipment = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(rentEquipment)
                .post("/api/rent_equipments")
                .then()
                .statusCode(201)
                .extract().as(RentEquipment.class);

        BeachVolleyballCourt trackAfterPut = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(beachVolleyballCourt)
                .put("/api/beach_volleyball_courts/{beachVolleyballCourtId}/rent_equipment/{rentEquipmentId}",
                        savedTrack.getId(), savedEquipment.getId())
                .then()
                .statusCode(200)
                .extract().as(BeachVolleyballCourt.class);

        //compare name of rent equipment
        assertThat(new ArrayList<>(trackAfterPut.getRentEquipmentNames()).get(0)).isEqualTo(savedEquipment.getName());

        TestUtils.clearRentEquipmentAfterTest(savedEquipment.getId());
        TestUtils.clearSportObjectAfterTest(savedTrack.getId());
    }

    @Test
    public void putEquipmentToBeachVolleyballCourtBeachVolleyballCourtNotFound() {
        BeachVolleyballCourt beachVolleyballCourt = TestUtils.createBeachVolleyballCourt();
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
                .body(beachVolleyballCourt)
                .put("/api/beach_volleyball_courts/0/rent_equipment/{rentEquipmentId}",
                        savedEquipment.getId())
                .then()
                .statusCode(404)
                .extract().response();

        //compare name of rent equipment
//        assertThat(new ArrayList<>(trackAfterPut.getRentEquipmentNames()).get(0)).isEqualTo(savedEquipment.getName());

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Beach volleyball court for given id not found. Try to search in all sport objects or change the id.");


        TestUtils.clearRentEquipmentAfterTest(savedEquipment.getId());
//        TestUtils.clearSportObjectAfterTest(savedTrack.getId());
    }

    @Test
    public void putEquipmentToBeachVolleyballCourtRentEquipmentNotFound() {
        BeachVolleyballCourt beachVolleyballCourt = TestUtils.createBeachVolleyballCourt();

        BeachVolleyballCourt savedTrack = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(beachVolleyballCourt)
                .post("/api/beach_volleyball_courts")
                .then()
                .statusCode(201)
                .extract().as(BeachVolleyballCourt.class);

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(beachVolleyballCourt)
                .put("/api/beach_volleyball_courts/{beachVolleyballCourtId}/rent_equipment/0",
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
