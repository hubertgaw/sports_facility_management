package pl.lodz.hubertgaw;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.lodz.hubertgaw.dto.TennisCourt;
import pl.lodz.hubertgaw.service.SportObjectService;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class TennisCourtResourceTest {

    @Inject
    SportObjectService sportObjectService;

    @Test
    public void getAll() {
        given()
                .when().get("/api/tennis_courts")
                .then()
                .statusCode(200);
    }

    @Test
    public void getById() {
        TennisCourt tennisCourt = createTennisCourt();
        TennisCourt saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tennisCourt)
                .post("/api/tennis_courts")
                .then()
                .statusCode(201)
                .extract().as(TennisCourt.class);
        TennisCourt got = given()
                .when().get("/api/tennis_courts/{tennisCourtId}", saved.getId())
                .then()
                .statusCode(200)
                .extract().as(TennisCourt.class);
        assertThat(saved).isEqualTo(got);
        clearTennisCourtAfterTest(saved.getId());
//        assertThat(saved.equals(got)).isTrue();
    }

    @Test
    public void post() {
        TennisCourt tennisCourt = createTennisCourt();
        TennisCourt saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tennisCourt)
                .post("/api/tennis_courts")
                .then()
                .statusCode(201)
                .extract().as(TennisCourt.class);
        assertThat(saved.getId()).isNotNull();
        clearTennisCourtAfterTest(saved.getId());
    }

    @Test
    public void postFailNoName() {
        TennisCourt tennisCourt = createTennisCourt();
        tennisCourt.setName(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tennisCourt)
                .post("/api/tennis_courts")
                .then()
                .statusCode(400);
    }

    @Test
    public void put() {
        TennisCourt tennisCourt = createTennisCourt();
        TennisCourt saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tennisCourt)
                .post("/api/tennis_courts")
                .then()
                .statusCode(201)
                .extract().as(TennisCourt.class);
        saved.setName("Updated");
        TennisCourt updated = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/tennis_courts")
                .then()
                .statusCode(200)
                .extract().as(TennisCourt.class);
        assertThat(updated.getName()).isEqualTo("Updated");
        clearTennisCourtAfterTest(saved.getId());

    }

    @Test
    public void putFailNoFullPrice() {
        TennisCourt tennisCourt = createTennisCourt();
        TennisCourt saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tennisCourt)
                .post("/api/tennis_courts")
                .then()
                .statusCode(201)
                .extract().as(TennisCourt.class);
        saved.setFullPrice(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/tennis_courts")
                .then()
                .statusCode(400);
        clearTennisCourtAfterTest(saved.getId());
    }

    private TennisCourt createTennisCourt() {
        TennisCourt tennisCourt = new TennisCourt();
        tennisCourt.setName(RandomStringUtils.randomAlphabetic(10));
        tennisCourt.setFullPrice(Double.valueOf(RandomStringUtils.randomNumeric(2)));
        return tennisCourt;
    }

    private void clearTennisCourtAfterTest(Integer id) {
        sportObjectService.deleteSportObjectById(id);
    }

}
