package pl.lodz.hubertgaw;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.lodz.hubertgaw.dto.Gym;
import pl.lodz.hubertgaw.service.SportObjectService;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class GymResourceTest {

    @Inject
    SportObjectService sportObjectService;

    @Test
    public void getAll() {
        given()
                .when().get("/api/gyms")
                .then()
                .statusCode(200);
    }

    @Test
    public void getById() {
        Gym gym = createGym();
        Gym saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(gym)
                .post("/api/gyms")
                .then()
                .statusCode(201)
                .extract().as(Gym.class);
        Gym got = given()
                .when().get("/api/gyms/{gymId}", saved.getId())
                .then()
                .statusCode(200)
                .extract().as(Gym.class);
        assertThat(saved).isEqualTo(got);
        clearGymAfterTest(saved.getId());
//        assertThat(saved.equals(got)).isTrue();
    }

    @Test
    public void post() {
        Gym gym = createGym();
        Gym saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(gym)
                .post("/api/gyms")
                .then()
                .statusCode(201)
                .extract().as(Gym.class);
        assertThat(saved.getId()).isNotNull();
        clearGymAfterTest(saved.getId());
    }

    @Test
    public void postFailNoName() {
        Gym gym = createGym();
        gym.setName(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(gym)
                .post("/api/gyms")
                .then()
                .statusCode(400);
    }

    @Test
    public void put() {
        Gym gym = createGym();
        Gym saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(gym)
                .post("/api/gyms")
                .then()
                .statusCode(201)
                .extract().as(Gym.class);
        saved.setName("Updated");
        Gym updated = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/gyms")
                .then()
                .statusCode(200)
                .extract().as(Gym.class);
        assertThat(updated.getName()).isEqualTo("Updated");
        clearGymAfterTest(saved.getId());

    }

    @Test
    public void putFailNoCapacity() {
        Gym gym = createGym();
        Gym saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(gym)
                .post("/api/gyms")
                .then()
                .statusCode(201)
                .extract().as(Gym.class);
        saved.setCapacity(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/gyms")
                .then()
                .statusCode(400);
        clearGymAfterTest(saved.getId());
    }

    private Gym createGym() {
        Gym gym = new Gym();
        gym.setName(RandomStringUtils.randomAlphabetic(10));
        gym.setFullPrice(Double.valueOf(RandomStringUtils.randomNumeric(2)));
        gym.setCapacity(Integer.valueOf(RandomStringUtils.randomNumeric(2)));
        gym.setSinglePrice(Double.valueOf(RandomStringUtils.randomNumeric(1)));
        return gym;
    }

    private void clearGymAfterTest(Integer id) {
        sportObjectService.deleteSportObjectById(id);
    }

}
