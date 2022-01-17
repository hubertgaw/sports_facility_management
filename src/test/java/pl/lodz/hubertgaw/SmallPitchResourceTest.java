package pl.lodz.hubertgaw;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.lodz.hubertgaw.dto.SmallPitch;
import pl.lodz.hubertgaw.service.SportObjectService;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class SmallPitchResourceTest {

    @Inject
    SportObjectService sportObjectService;

    @Test
    public void getAll() {
        given()
                .when().get("/api/small_pitches")
                .then()
                .statusCode(200);
    }

    @Test
    public void getById() {
        SmallPitch smallPitch = createSmallPitch();
        SmallPitch saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(smallPitch)
                .post("/api/small_pitches")
                .then()
                .statusCode(201)
                .extract().as(SmallPitch.class);
        SmallPitch got = given()
                .when().get("/api/small_pitches/{smallPitchId}", saved.getId())
                .then()
                .statusCode(200)
                .extract().as(SmallPitch.class);
        assertThat(saved).isEqualTo(got);
        clearSmallPitchAfterTest(saved.getId());
//        assertThat(saved.equals(got)).isTrue();
    }

    @Test
    public void post() {
        SmallPitch smallPitch = createSmallPitch();
        SmallPitch saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(smallPitch)
                .post("/api/small_pitches")
                .then()
                .statusCode(201)
                .extract().as(SmallPitch.class);
        assertThat(saved.getId()).isNotNull();
        clearSmallPitchAfterTest(saved.getId());
    }

    @Test
    public void postFailNoName() {
        SmallPitch smallPitch = createSmallPitch();
        smallPitch.setName(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(smallPitch)
                .post("/api/small_pitches")
                .then()
                .statusCode(400);
    }

    @Test
    public void put() {
        SmallPitch smallPitch = createSmallPitch();
        SmallPitch saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(smallPitch)
                .post("/api/small_pitches")
                .then()
                .statusCode(201)
                .extract().as(SmallPitch.class);
        saved.setName("Updated");
        SmallPitch updated = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/small_pitches")
                .then()
                .statusCode(200)
                .extract().as(SmallPitch.class);
        assertThat(updated.getName()).isEqualTo("Updated");
        clearSmallPitchAfterTest(saved.getId());
    }

    @Test
    public void putIsFullRentedNull() {
        SmallPitch smallPitch = createSmallPitch();
        SmallPitch saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(smallPitch)
                .post("/api/small_pitches")
                .then()
                .statusCode(201)
                .extract().as(SmallPitch.class);
        saved.setIsFullRented(null);
        SmallPitch updated = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/small_pitches")
                .then()
                .statusCode(200)
                .extract().as(SmallPitch.class);
        assertThat(updated.getIsFullRented()).isEqualTo(null);
        clearSmallPitchAfterTest(saved.getId());
    }


    @Test
    public void putFailNoHalfPitchPrice() {
        SmallPitch smallPitch = createSmallPitch();
        SmallPitch saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(smallPitch)
                .post("/api/small_pitches")
                .then()
                .statusCode(201)
                .extract().as(SmallPitch.class);
        saved.setHalfPitchPrice(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/small_pitches")
                .then()
                .statusCode(400);
        clearSmallPitchAfterTest(saved.getId());
    }



    private SmallPitch createSmallPitch() {
        SmallPitch smallPitch = new SmallPitch();
        smallPitch.setName(RandomStringUtils.randomAlphabetic(10));
        smallPitch.setFullPrice(Double.valueOf(RandomStringUtils.randomNumeric(2)));
        smallPitch.setIsFullRented(Boolean.valueOf(RandomStringUtils.randomNumeric(2)));
        smallPitch.setHalfPitchPrice(Double.valueOf(RandomStringUtils.randomNumeric(1)));
        return smallPitch;
    }

    private void clearSmallPitchAfterTest(Integer id) {
        sportObjectService.deleteSportObjectById(id);
    }

}
