package pl.lodz.hubertgaw;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.lodz.hubertgaw.dto.ClimbingWall;
import pl.lodz.hubertgaw.service.SportObjectService;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class ClimbingWallResourceTest {

    @Inject
    SportObjectService sportObjectService;

    @Test
    public void getAll() {
        given()
                .when().get("/api/climbing_walls")
                .then()
                .statusCode(200);
    }

    @Test
    public void getById() {
        ClimbingWall climbingWall = createClimbingWall();
        ClimbingWall saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(climbingWall)
                .post("/api/climbing_walls")
                .then()
                .statusCode(201)
                .extract().as(ClimbingWall.class);
        ClimbingWall got = given()
                .when().get("/api/climbing_walls/{climbingWallId}", saved.getId())
                .then()
                .statusCode(200)
                .extract().as(ClimbingWall.class);
        assertThat(saved).isEqualTo(got);
        clearClimbingWallAfterTest(saved.getId());
//        assertThat(saved.equals(got)).isTrue();
    }

    @Test
    public void post() {
        ClimbingWall climbingWall = createClimbingWall();
        ClimbingWall saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(climbingWall)
                .post("/api/climbing_walls")
                .then()
                .statusCode(201)
                .extract().as(ClimbingWall.class);
        assertThat(saved.getId()).isNotNull();
        clearClimbingWallAfterTest(saved.getId());
    }

    @Test
    public void postFailNoName() {
        ClimbingWall climbingWall = createClimbingWall();
        climbingWall.setName(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(climbingWall)
                .post("/api/climbing_walls")
                .then()
                .statusCode(400);
    }

    @Test
    public void put() {
        ClimbingWall climbingWall = createClimbingWall();
        ClimbingWall saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(climbingWall)
                .post("/api/climbing_walls")
                .then()
                .statusCode(201)
                .extract().as(ClimbingWall.class);
        saved.setName("Updated");
        ClimbingWall updated = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/climbing_walls")
                .then()
                .statusCode(200)
                .extract().as(ClimbingWall.class);
        assertThat(updated.getName()).isEqualTo("Updated");
        clearClimbingWallAfterTest(saved.getId());

    }

    @Test
    public void putFailNoCapacity() {
        ClimbingWall climbingWall = createClimbingWall();
        ClimbingWall saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(climbingWall)
                .post("/api/climbing_walls")
                .then()
                .statusCode(201)
                .extract().as(ClimbingWall.class);
        saved.setCapacity(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/climbing_walls")
                .then()
                .statusCode(400);
        clearClimbingWallAfterTest(saved.getId());
    }

    private ClimbingWall createClimbingWall() {
        ClimbingWall climbingWall = new ClimbingWall();
        climbingWall.setName(RandomStringUtils.randomAlphabetic(10));
        climbingWall.setFullPrice(Double.valueOf(RandomStringUtils.randomNumeric(2)));
        climbingWall.setCapacity(Integer.valueOf(RandomStringUtils.randomNumeric(2)));
        climbingWall.setSinglePrice(Double.valueOf(RandomStringUtils.randomNumeric(1)));
        return climbingWall;
    }

    private void clearClimbingWallAfterTest(Integer id) {
        sportObjectService.deleteSportObjectById(id);
    }

}
