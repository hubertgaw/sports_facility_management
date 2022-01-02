package pl.lodz.hubertgaw;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.lodz.hubertgaw.dto.FullSizePitch;
import pl.lodz.hubertgaw.service.SportObjectService;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class FullSizePitchResourceTest {

    @Inject
    SportObjectService sportObjectService;

    @Test
    public void getAll() {
        given()
                .when().get("/api/full_size_pitches")
                .then()
                .statusCode(200);
    }

    @Test
    public void getById() {
        FullSizePitch fullSizePitch = createFullSizePitch();
        FullSizePitch saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(fullSizePitch)
                .post("/api/full_size_pitches")
                .then()
                .statusCode(201)
                .extract().as(FullSizePitch.class);
        FullSizePitch got = given()
                .when().get("/api/full_size_pitches/{fullSizePitchId}", saved.getId())
                .then()
                .statusCode(200)
                .extract().as(FullSizePitch.class);
        assertThat(saved).isEqualTo(got);
        clearFullSizePitchAfterTest(saved.getId());
//        assertThat(saved.equals(got)).isTrue();
    }

    @Test
    public void post() {
        FullSizePitch fullSizePitch = createFullSizePitch();
        FullSizePitch saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(fullSizePitch)
                .post("/api/full_size_pitches")
                .then()
                .statusCode(201)
                .extract().as(FullSizePitch.class);
        assertThat(saved.getId()).isNotNull();
        clearFullSizePitchAfterTest(saved.getId());
    }

    @Test
    public void postFailNoName() {
        FullSizePitch fullSizePitch = createFullSizePitch();
        fullSizePitch.setName(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(fullSizePitch)
                .post("/api/full_size_pitches")
                .then()
                .statusCode(400);
    }

    @Test
    public void put() {
        FullSizePitch fullSizePitch = createFullSizePitch();
        FullSizePitch saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(fullSizePitch)
                .post("/api/full_size_pitches")
                .then()
                .statusCode(201)
                .extract().as(FullSizePitch.class);
        saved.setName("Updated");
        FullSizePitch updated = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/full_size_pitches")
                .then()
                .statusCode(200)
                .extract().as(FullSizePitch.class);
        assertThat(updated.getName()).isEqualTo("Updated");
        clearFullSizePitchAfterTest(saved.getId());

    }

    @Test
    public void putIsFullRentedNull() {
        FullSizePitch fullSizePitch = createFullSizePitch();
        FullSizePitch saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(fullSizePitch)
                .post("/api/full_size_pitches")
                .then()
                .statusCode(201)
                .extract().as(FullSizePitch.class);
        saved.setIsFullRented(null);
        FullSizePitch updated = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/full_size_pitches")
                .then()
                .statusCode(200)
                .extract().as(FullSizePitch.class);
        assertThat(updated.getIsFullRented()).isEqualTo(null);
        clearFullSizePitchAfterTest(saved.getId());

    }

    @Test
    public void putFailNoFullPrice() {
        FullSizePitch fullSizePitch = createFullSizePitch();
        FullSizePitch saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(fullSizePitch)
                .post("/api/full_size_pitches")
                .then()
                .statusCode(201)
                .extract().as(FullSizePitch.class);
        saved.setFullPrice(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/full_size_pitches")
                .then()
                .statusCode(400);
        clearFullSizePitchAfterTest(saved.getId());
    }

    private FullSizePitch createFullSizePitch() {
        FullSizePitch fullSizePitch = new FullSizePitch();
        fullSizePitch.setName(RandomStringUtils.randomAlphabetic(10));
        fullSizePitch.setFullPrice(Double.valueOf(RandomStringUtils.randomNumeric(2)));
        fullSizePitch.setIsFullRented(Boolean.valueOf(RandomStringUtils.randomNumeric(2)));
        fullSizePitch.setHalfPitchPrice(Double.valueOf(RandomStringUtils.randomNumeric(1)));
        return fullSizePitch;
    }

    private void clearFullSizePitchAfterTest(Integer id) {
        sportObjectService.deleteSportObjectById(id);
    }

}
