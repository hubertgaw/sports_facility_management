package pl.lodz.hubertgaw;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import pl.lodz.hubertgaw.dto.AthleticsTrack;
import pl.lodz.hubertgaw.dto.RentEquipment;
import pl.lodz.hubertgaw.dto.SportObject;
import pl.lodz.hubertgaw.service.RentEquipmentService;
import pl.lodz.hubertgaw.service.SportObjectService;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class SportObjectResourceTest {

    @Inject
    SportObjectService sportObjectService;
    @Inject
    RentEquipmentService rentEquipmentService;

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
        clearAthleticsTrackAfterTest(saved.getId());
//        assertThat(saved.equals(got)).isTrue();
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

        clearAthleticsTrackAfterTest(savedAthleticsTrack.getId());
        clearRentEquipmentAfterTest(savedRentEquipment.getId());

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
                .statusCode(200);
        //check if sportObject with that id is not found:
        given()
                .when().get("/api/sport_objects/{sportObjectId}", saved.getId())
                .then()
                .statusCode(404);
    }



    //athletics track is used because we need specific implementation of SportObject (can be anything else for example DartRoom etc.)
    private AthleticsTrack createAthleticsTrack() {
        AthleticsTrack athleticsTrack = new AthleticsTrack();
        athleticsTrack.setName(RandomStringUtils.randomAlphabetic(10));
        athleticsTrack.setFullPrice(Double.valueOf(RandomStringUtils.randomNumeric(2)));
        athleticsTrack.setCapacity(Integer.valueOf(RandomStringUtils.randomNumeric(2)));
        athleticsTrack.setSingleTrackPrice(Double.valueOf(RandomStringUtils.randomNumeric(1)));
        return athleticsTrack;
    }

    private RentEquipment createRentEquipment() {
        RentEquipment rentEquipment = new RentEquipment();
        rentEquipment.setName(RandomStringUtils.randomAlphabetic(10));
        rentEquipment.setPrice(Double.valueOf(RandomStringUtils.randomNumeric(2)));
        return rentEquipment;
    }

    private void clearAthleticsTrackAfterTest(Integer id) {
        sportObjectService.deleteSportObjectById(id);
    }

    private void clearRentEquipmentAfterTest(Integer id) {
        rentEquipmentService.deleteRentEquipmentById(id);
    }

}
