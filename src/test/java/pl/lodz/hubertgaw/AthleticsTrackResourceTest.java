package pl.lodz.hubertgaw;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import pl.lodz.hubertgaw.dto.AthleticsTrack;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class AthleticsTrackResourceTest {

    @Test
    public void getAll() {
        given()
                .when().get("/api/athletics_tracks")
                .then()
                .statusCode(200);
    }

    @Test
    public void getById() {
        AthleticsTrack athleticsTrack = createAthleticsTrack();
        AthleticsTrack saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(athleticsTrack)
                .post("/api/athletics_tracks")
                .then()
                .statusCode(201)
                .extract().as(AthleticsTrack.class);
        AthleticsTrack got = given()
                .when().get("/api/athletics_tracks/{athleticsTrackId}", saved.getId())
                .then()
                .statusCode(200)
                .extract().as(AthleticsTrack.class);
        assertThat(saved).isEqualTo(got);
//        assertThat(saved.equals(got)).isTrue();
    }

    @Test
    public void post() {
        AthleticsTrack athleticsTrack = createAthleticsTrack();
        AthleticsTrack saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(athleticsTrack)
                .post("/api/athletics_tracks")
                .then()
                .statusCode(201)
                .extract().as(AthleticsTrack.class);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    public void postFailNoName() {
        AthleticsTrack athleticsTrack = createAthleticsTrack();
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
        AthleticsTrack athleticsTrack = createAthleticsTrack();
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
    }

    @Test
    public void putFailNoCapacity() {
        AthleticsTrack athleticsTrack = createAthleticsTrack();
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
    }

    private AthleticsTrack createAthleticsTrack() {
        AthleticsTrack athleticsTrack = new AthleticsTrack();
        athleticsTrack.setName(RandomStringUtils.randomAlphabetic(10));
        athleticsTrack.setFullPrice(Double.valueOf(RandomStringUtils.randomNumeric(2)));
        athleticsTrack.setCapacity(Integer.valueOf(RandomStringUtils.randomNumeric(2)));
        athleticsTrack.setSingleTrackPrice(Double.valueOf(RandomStringUtils.randomNumeric(1)));
        return athleticsTrack;
    }

}
