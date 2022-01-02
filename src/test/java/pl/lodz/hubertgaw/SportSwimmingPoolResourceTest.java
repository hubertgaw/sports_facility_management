package pl.lodz.hubertgaw;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.lodz.hubertgaw.dto.SportSwimmingPool;
import pl.lodz.hubertgaw.service.SportObjectService;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class SportSwimmingPoolResourceTest {

    @Inject
    SportObjectService sportObjectService;

    @Test
    public void getAll() {
        given()
                .when().get("/api/sport_swimming_pools")
                .then()
                .statusCode(200);
    }

    @Test
    public void getById() {
        SportSwimmingPool sportSwimmingPool = createSportSwimmingPool();
        SportSwimmingPool saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportSwimmingPool)
                .post("/api/sport_swimming_pools")
                .then()
                .statusCode(201)
                .extract().as(SportSwimmingPool.class);
        SportSwimmingPool got = given()
                .when().get("/api/sport_swimming_pools/{sportSwimmingPoolId}", saved.getId())
                .then()
                .statusCode(200)
                .extract().as(SportSwimmingPool.class);
        assertThat(saved).isEqualTo(got);
        clearSportSwimmingPoolAfterTest(saved.getId());
//        assertThat(saved.equals(got)).isTrue();
    }

    @Test
    public void post() {
        SportSwimmingPool sportSwimmingPool = createSportSwimmingPool();
        SportSwimmingPool saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportSwimmingPool)
                .post("/api/sport_swimming_pools")
                .then()
                .statusCode(201)
                .extract().as(SportSwimmingPool.class);
        assertThat(saved.getId()).isNotNull();
        clearSportSwimmingPoolAfterTest(saved.getId());
    }

    @Test
    public void postFailNoName() {
        SportSwimmingPool sportSwimmingPool = createSportSwimmingPool();
        sportSwimmingPool.setName(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportSwimmingPool)
                .post("/api/sport_swimming_pools")
                .then()
                .statusCode(400);
    }

    @Test
    public void put() {
        SportSwimmingPool sportSwimmingPool = createSportSwimmingPool();
        SportSwimmingPool saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportSwimmingPool)
                .post("/api/sport_swimming_pools")
                .then()
                .statusCode(201)
                .extract().as(SportSwimmingPool.class);
        saved.setName("Updated");
        SportSwimmingPool updated = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/sport_swimming_pools")
                .then()
                .statusCode(200)
                .extract().as(SportSwimmingPool.class);
        assertThat(updated.getName()).isEqualTo("Updated");
        clearSportSwimmingPoolAfterTest(saved.getId());

    }

    @Test
    public void putFailNoTracksNumber() {
        SportSwimmingPool sportSwimmingPool = createSportSwimmingPool();
        SportSwimmingPool saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(sportSwimmingPool)
                .post("/api/sport_swimming_pools")
                .then()
                .statusCode(201)
                .extract().as(SportSwimmingPool.class);
        saved.setTracksNumber(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/sport_swimming_pools")
                .then()
                .statusCode(400);
        clearSportSwimmingPoolAfterTest(saved.getId());
    }

    private SportSwimmingPool createSportSwimmingPool() {
        SportSwimmingPool sportSwimmingPool = new SportSwimmingPool();
        sportSwimmingPool.setName(RandomStringUtils.randomAlphabetic(10));
        sportSwimmingPool.setFullPrice(Double.valueOf(RandomStringUtils.randomNumeric(2)));
        sportSwimmingPool.setTracksNumber(Integer.valueOf(RandomStringUtils.randomNumeric(2)));
        sportSwimmingPool.setTrackPrice(Double.valueOf(RandomStringUtils.randomNumeric(1)));
        return sportSwimmingPool;
    }

    private void clearSportSwimmingPoolAfterTest(Integer id) {
        sportObjectService.deleteSportObjectById(id);
    }

}
