package pl.lodz.hubertgaw;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import pl.lodz.hubertgaw.dto.User;
import pl.lodz.hubertgaw.repository.UserRepository;
import pl.lodz.hubertgaw.utils.TestUtils;

import javax.inject.Inject;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@TestSecurity(authorizationEnabled = false)
public class UserResourceTest {

    @Inject
    UserRepository userRepository;

    @Test
    public void getAll() {
        given()
                .when().get("/api/users")
                .then()
                .statusCode(200);
    }

    @Test
    public void getById() {
        User user = TestUtils.createUser();

        User savedUser = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(user)
                .post("/api/users")
                .then()
                .statusCode(201)
                .extract().as(User.class);
        User gotUser = given()
                .when().get("/api/users/id/{userId}", savedUser.getId())
                .then()
                .statusCode(200)
                .extract().as(User.class);

        assertThat(savedUser).isEqualTo(gotUser);

        TestUtils.clearUserAfterTest(savedUser.getId());
    }

    @Test
    public void getByEmail() {
        User user = TestUtils.createUser();

        User savedUser = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(user)
                .post("/api/users")
                .then()
                .statusCode(201)
                .extract().as(User.class);
        User gotUser = given()
                .when().get("/api/users/email/{email}", savedUser.getEmail())
                .then()
                .statusCode(200)
                .extract().as(User.class);

        assertThat(savedUser).isEqualTo(gotUser);

        TestUtils.clearUserAfterTest(savedUser.getId());
    }

    @Test
    public void getByRole() {
        User user = TestUtils.createUser();

        User savedUser = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(user)
                .post("/api/users")
                .then()
                .statusCode(201)
                .extract().as(User.class);

        //new user should have "USER" role by default.
        List<User> gotUser = given()
                .when().get("/api/users/role/{role}", savedUser.getRoles().stream().findFirst().get().name())
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath().getList(".", User.class);

//        assertThat(gotUser.size()).isEqualTo(1);
        assertThat(gotUser.get(0).getRoles().stream().findFirst().get().name()).isEqualTo("USER");

        TestUtils.clearUserAfterTest(savedUser.getId());
    }

    @Test
    public void post() {
        User user = TestUtils.createUser();

        User savedUser = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(user)
                .post("/api/users")
                .then()
                .statusCode(201)
                .extract().as(User.class);
        assertThat(savedUser.getId()).isNotNull();

        TestUtils.clearUserAfterTest(savedUser.getId());
    }

    @Test
    public void put() {
        User user = TestUtils.createUser();

        User savedUser = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(user)
                .post("/api/users")
                .then()
                .statusCode(201)
                .extract().as(User.class);
        savedUser.setLastName("Updated");
        User updated = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(savedUser)
                .put("/api/users")
                .then()
                .statusCode(200)
                .extract().as(User.class);
        assertThat(updated.getId()).isNotNull();
        assertThat(updated.getLastName()).isEqualTo("Updated");

        TestUtils.clearUserAfterTest(savedUser.getId());

    }

    public void delete() {
        User user = TestUtils.createUser();

        User savedUser = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(user)
                .post("/api/users")
                .then()
                .statusCode(201)
                .extract().as(User.class);
        given()
                .when()
                .delete("api/users/{userId}", savedUser.getId())
                .then()
                .statusCode(204);
        //check if user with that id is not found:
        given()
                .when().get("/api/users/{userId}", savedUser.getId())
                .then()
                .statusCode(404);

        TestUtils.clearUserAfterTest(savedUser.getId());

    }

}
