package pl.lodz.hubertgaw;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.lodz.hubertgaw.dto.SportsHall;
import pl.lodz.hubertgaw.service.SportObjectService;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class SportsHallResourceTest {

    @Inject
    SportObjectService sportObjectService;

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
        clearSportsHallAfterTest(saved.getId());
//        assertThat(saved.equals(got)).isTrue();
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
        clearSportsHallAfterTest(saved.getId());
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
        clearSportsHallAfterTest(saved.getId());

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
        clearSportsHallAfterTest(saved.getId());
    }

    private SportsHall createSportsHall() {
        SportsHall sportsHall = new SportsHall();
        sportsHall.setName(RandomStringUtils.randomAlphabetic(10));
        sportsHall.setFullPrice(Double.valueOf(RandomStringUtils.randomNumeric(2)));
        sportsHall.setSectorsNumber(Integer.valueOf(RandomStringUtils.randomNumeric(2)));
        sportsHall.setSectorPrice(Double.valueOf(RandomStringUtils.randomNumeric(1)));
        return sportsHall;
    }

    private void clearSportsHallAfterTest(Integer id) {
        sportObjectService.deleteSportObjectById(id);
    }

}
