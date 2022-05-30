package pl.lodz.hubertgaw;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import pl.lodz.hubertgaw.dto.AthleticsTrack;
import pl.lodz.hubertgaw.dto.RentEquipment;
import pl.lodz.hubertgaw.service.RentEquipmentService;
import pl.lodz.hubertgaw.service.SportObjectService;
import pl.lodz.hubertgaw.utils.TestUtils;

import javax.inject.Inject;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@TestSecurity(authorizationEnabled = false)
public class AthleticsTrackResourceTest {

    @Inject
    SportObjectService sportObjectService;
    @Inject
    RentEquipmentService rentEquipmentService;

    @Test
    public void getAll() {
        given()
                .when().get("/api/athletics_tracks")
                .then()
                .statusCode(200);
    }

    @Test
    public void getById() {
        AthleticsTrack athleticsTrack = TestUtils.createAthleticsTrack();
        AthleticsTrack saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(athleticsTrack)
                .post("/api/athletics_tracks")
                .then()
                .statusCode(201)
                .extract().as(AthleticsTrack.class);
//        AthleticsTrack saved = addAthleticsTrackToRepo();
        AthleticsTrack got = given()
                .when().get("/api/athletics_tracks/{athleticsTrackId}", saved.getId())
                .then()
                .statusCode(200)
                .extract().as(AthleticsTrack.class);
        assertThat(saved).isEqualTo(got);
        TestUtils.clearSportObjectAfterTest(saved.getId());
//        assertThat(saved.equals(got)).isTrue();
    }

    @Test
    public void getByIdFailNotFound() {
        Response response = given()
                .when().get("/api/athletics_tracks/{athleticsTrackId}", 0)// there will be for sure no object with id 0
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Athletics track for given id not found. Try to search in all sport objects or change the id.");

    }


    @Test
    public void post() {
        AthleticsTrack athleticsTrack = TestUtils.createAthleticsTrack();
        AthleticsTrack saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(athleticsTrack)
                .post("/api/athletics_tracks")
                .then()
                .statusCode(201)
                .extract().as(AthleticsTrack.class);
        assertThat(saved.getId()).isNotNull();
        TestUtils.clearSportObjectAfterTest(saved.getId());
    }

    @Test
    public void postFailNoName() {
        AthleticsTrack athleticsTrack = TestUtils.createAthleticsTrack();
        athleticsTrack.setName(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(athleticsTrack)
                .post("/api/athletics_tracks")
                .then()
                .statusCode(400);
    }

    @Test
    public void put() {
        AthleticsTrack athleticsTrack = TestUtils.createAthleticsTrack();
        AthleticsTrack saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(athleticsTrack)
                .post("/api/athletics_tracks")
                .then()
                .statusCode(201)
                .extract().as(AthleticsTrack.class);
        saved.setName("Updated");
        AthleticsTrack updated = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/athletics_tracks")
                .then()
                .statusCode(200)
                .extract().as(AthleticsTrack.class);
        assertThat(updated.getName()).isEqualTo("Updated");
        TestUtils.clearSportObjectAfterTest(saved.getId());

    }

    @Test
    public void putFailNoCapacity() {
        AthleticsTrack athleticsTrack = TestUtils.createAthleticsTrack();
        AthleticsTrack saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(athleticsTrack)
                .post("/api/athletics_tracks")
                .then()
                .statusCode(201)
                .extract().as(AthleticsTrack.class);
        saved.setCapacity(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/athletics_tracks")
                .then()
                .statusCode(400);
        TestUtils.clearSportObjectAfterTest(saved.getId());
    }

    @Test
    public void postFailDuplicateName() {
        AthleticsTrack athleticsTrack = TestUtils.createAthleticsTrack();
        AthleticsTrack saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(athleticsTrack)
                .post("/api/athletics_tracks")
                .then()
                .extract().as(AthleticsTrack.class);

        AthleticsTrack athleticsTrack2 = TestUtils.createAthleticsTrack();
        athleticsTrack2.setName(athleticsTrack.getName());

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(athleticsTrack2)
                .post("/api/athletics_tracks")
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
        AthleticsTrack athleticsTrack = TestUtils.createAthleticsTrack();
        AthleticsTrack saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(athleticsTrack)
                .post("/api/athletics_tracks")
                .then()
                .statusCode(201)
                .extract().as(AthleticsTrack.class);
        int id = saved.getId(); // we will need that to delete after test.
        saved.setId(null);
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/athletics_tracks")
                .then()
                .statusCode(400)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(actualExceptionMessage).isEqualTo("Id for updating athletics track cannot be null");

        TestUtils.clearSportObjectAfterTest(id);
    }

    @Test
    public void putFailNotFound() {
        AthleticsTrack athleticsTrack = TestUtils.createAthleticsTrack();
        //there will never be id 0 in repo.
        athleticsTrack.setId(0);
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(athleticsTrack)
                .put("/api/athletics_tracks")
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Athletics track for given id not found. Try to search in all sport objects or change the id.");
    }

    @Test
    public void putEquipmentToAthleticsTrack() {
        AthleticsTrack athleticsTrack = TestUtils.createAthleticsTrack();
        RentEquipment rentEquipment = TestUtils.createRentEquipment();

        AthleticsTrack savedTrack = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(athleticsTrack)
                .post("/api/athletics_tracks")
                .then()
                .statusCode(201)
                .extract().as(AthleticsTrack.class);

        RentEquipment savedEquipment = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(rentEquipment)
                .post("/api/rent_equipments")
                .then()
                .statusCode(201)
                .extract().as(RentEquipment.class);

        AthleticsTrack trackAfterPut = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(athleticsTrack)
                .put("/api/athletics_tracks/{athleticsTrackId}/rent_equipment/{rentEquipmentId}",
                        savedTrack.getId(), savedEquipment.getId())
                .then()
                .statusCode(200)
                .extract().as(AthleticsTrack.class);

        //compare name of rent equipment
        assertThat(new ArrayList<>(trackAfterPut.getRentEquipmentNames()).get(0)).isEqualTo(savedEquipment.getName());

        TestUtils.clearRentEquipmentAfterTest(savedEquipment.getId());
        TestUtils.clearSportObjectAfterTest(savedTrack.getId());
    }

    @Test
    public void putEquipmentToAthleticsTrackAthleticsTrackNotFound() {
        AthleticsTrack athleticsTrack = TestUtils.createAthleticsTrack();
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
                .body(athleticsTrack)
                .put("/api/athletics_tracks/0/rent_equipment/{rentEquipmentId}",
                        savedEquipment.getId())
                .then()
                .statusCode(404)
                .extract().response();

        //compare name of rent equipment
//        assertThat(new ArrayList<>(trackAfterPut.getRentEquipmentNames()).get(0)).isEqualTo(savedEquipment.getName());

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Athletics track for given id not found. Try to search in all sport objects or change the id.");


        TestUtils.clearRentEquipmentAfterTest(savedEquipment.getId());
//        clearAthleticsTrackAfterTest(savedTrack.getId());
    }

    @Test
    public void putEquipmentToAthleticsTrackRentEquipmentNotFound() {
        AthleticsTrack athleticsTrack = TestUtils.createAthleticsTrack();

        AthleticsTrack savedTrack = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(athleticsTrack)
                .post("/api/athletics_tracks")
                .then()
                .statusCode(201)
                .extract().as(AthleticsTrack.class);

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(athleticsTrack)
                .put("/api/athletics_tracks/{athleticsTrackId}/rent_equipment/0",
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
