package pl.lodz.hubertgaw;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import pl.lodz.hubertgaw.dto.AthleticsTrack;
import pl.lodz.hubertgaw.dto.RentEquipment;
import pl.lodz.hubertgaw.dto.SportObject;
import pl.lodz.hubertgaw.service.RentEquipmentService;
import pl.lodz.hubertgaw.service.SportObjectService;
import pl.lodz.hubertgaw.utils.TestUtils;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static pl.lodz.hubertgaw.utils.TestUtils.createAthleticsTrack;
import static pl.lodz.hubertgaw.utils.TestUtils.createRentEquipment;

@QuarkusTest
@TestSecurity(authorizationEnabled = false)
public class SportObjectResourceTest {

    @Test
    public void getAll() {
        given()
                .when().get("/api/sport_objects")
                .then()
                .statusCode(200);
    }

    @Test
    public void getById() {
        AthleticsTrack athleticsTrack = createAthleticsTrack();
        SportObject saved = (SportObject) given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(athleticsTrack)
                .post("/api/athletics_tracks")
                .then()
                .statusCode(201)
                .extract().as(AthleticsTrack.class);
        SportObject got = (SportObject) given()
                .when().get("/api/sport_objects/{sportObjectId}", saved.getId())
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
                .when().get("/api/sport_objects/{sportObjectId}", 0)// there will be for sure no object with id 0
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Sport object for given id not found");

    }

    @Test
    public void getByName() {
        AthleticsTrack athleticsTrack = createAthleticsTrack();
        SportObject saved = (SportObject) given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(athleticsTrack)
                .post("/api/athletics_tracks")
                .then()
                .statusCode(201)
                .extract().as(AthleticsTrack.class);
        SportObject got = (SportObject) given()
                .when().get("/api/sport_objects/name/{sportObjectName}", saved.getName())
                .then()
                .statusCode(200)
                .extract().as(AthleticsTrack.class);
        assertThat(saved).isEqualTo(got);
        TestUtils.clearSportObjectAfterTest(saved.getId());
//        assertThat(saved.equals(got)).isTrue();
    }

    @Test
    public void getByNameFailNotFound() {
        Response response = given()
                .when().get("/api/sport_objects/name/{sportObjectName}", "randomNamecdjfbdhas")// there will be for sure no object with name provided
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Sport object for given name not found");

    }


    @Test
    public void putEquipmentToObject() {
        AthleticsTrack athleticsTrack = createAthleticsTrack();
        AthleticsTrack savedAthleticsTrack = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(athleticsTrack)
                .post("/api/athletics_tracks")
                .then()
                .statusCode(201)
                .extract().as(AthleticsTrack.class);

        RentEquipment rentEquipment = createRentEquipment();
        RentEquipment savedRentEquipment = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(rentEquipment)
                .post("/api/rent_equipments")
                .then()
                .statusCode(201)
                .extract().as(RentEquipment.class);

        SportObject sportObjectWithEquipment = (SportObject) given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .put("/api/sport_objects/{sportObjectId}/rent_equipment/{rentEquipmentId}",
                        savedAthleticsTrack.getId(), savedRentEquipment.getId())
                .then()
                .statusCode(200)
                .extract().as(AthleticsTrack.class);

        //comparing by names because sportObject has set of Strings
        assertThat(sportObjectWithEquipment.getRentEquipmentNames().stream().findFirst().get())
                .isEqualTo(savedRentEquipment.getName());

        TestUtils.clearSportObjectAfterTest(savedAthleticsTrack.getId());
        TestUtils.clearRentEquipmentAfterTest(savedRentEquipment.getId());

    }

    @Test
    public void delete() {
        AthleticsTrack sportObject = createAthleticsTrack();
        AthleticsTrack saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportObject)
                .post("/api/athletics_tracks")
                .then()
                .statusCode(201)
                .extract().as(AthleticsTrack.class);
        given()
                .when()
                .delete("api/sport_objects/{sportObjectId}", saved.getId())
                .then()
                .statusCode(204);
        //check if sportObject with that id is not found:
        given()
                .when().get("/api/sport_objects/{sportObjectId}", saved.getId())
                .then()
                .statusCode(404);
    }

    @Test
    public void deleteFailNotFound() {
        //there will never be id 0 in repo.
        Response response = given()
                .when()
                .delete("api/sport_objects/0")
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Sport object for given id not found");
    }

    @Test
    public void putEquipmentToSportObjectSportObjectNotFound() {
        AthleticsTrack sportObject = TestUtils.createAthleticsTrack();
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
                .body(sportObject)
                .put("/api/sport_objects/0/rent_equipment/{rentEquipmentId}",
                        savedEquipment.getId())
                .then()
                .statusCode(404)
                .extract().response();

        //compare name of rent equipment
//        assertThat(new ArrayList<>(trackAfterPut.getRentEquipmentNames()).get(0)).isEqualTo(savedEquipment.getName());

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Sport object for given id not found");


        TestUtils.clearRentEquipmentAfterTest(savedEquipment.getId());
//        TestUtils.clearSportObjectAfterTest(savedTrack.getId());
    }

    @Test
    public void putEquipmentToSportObjectRentEquipmentNotFound() {
        AthleticsTrack sportObject = TestUtils.createAthleticsTrack();

        AthleticsTrack savedTrack = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportObject)
                .post("/api/athletics_tracks")
                .then()
                .statusCode(201)
                .extract().as(AthleticsTrack.class);

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportObject)
                .put("/api/sport_objects/{athleticsTrackId}/rent_equipment/0",
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
