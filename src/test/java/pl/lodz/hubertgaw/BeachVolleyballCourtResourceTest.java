package pl.lodz.hubertgaw;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import pl.lodz.hubertgaw.dto.BeachVolleyballCourt;
import pl.lodz.hubertgaw.service.SportObjectService;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class BeachVolleyballCourtResourceTest {

    @Inject
    SportObjectService sportObjectService;

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
        clearBeachVolleyballCourtAfterTest(saved.getId());
//        assertThat(saved.equals(got)).isTrue();
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
        clearBeachVolleyballCourtAfterTest(saved.getId());
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
        clearBeachVolleyballCourtAfterTest(saved.getId());

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
        clearBeachVolleyballCourtAfterTest(saved.getId());
    }

    private BeachVolleyballCourt createBeachVolleyballCourt() {
        BeachVolleyballCourt beachVolleyballCourt = new BeachVolleyballCourt();
        beachVolleyballCourt.setName(RandomStringUtils.randomAlphabetic(10));
        beachVolleyballCourt.setFullPrice(Double.valueOf(RandomStringUtils.randomNumeric(2)));
        return beachVolleyballCourt;
    }

    private void clearBeachVolleyballCourtAfterTest(Integer id) {
        sportObjectService.deleteSportObjectById(id);
    }

}
