package pl.lodz.hubertgaw;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import pl.lodz.hubertgaw.dto.RentEquipment;
import pl.lodz.hubertgaw.dto.RentEquipment;
import pl.lodz.hubertgaw.service.RentEquipmentService;
import pl.lodz.hubertgaw.utils.TestUtils;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static pl.lodz.hubertgaw.utils.TestUtils.createRentEquipment;

@QuarkusTest
public class RentEquipmentResourceTest {

    @Inject
    RentEquipmentService rentEquipmentService;

    @Test
    public void getAll() {
        given()
                .when().get("/api/rent_equipments")
                .then()
                .statusCode(200);
    }

    @Test
    public void getById() {
        RentEquipment rentEquipment = createRentEquipment();
        RentEquipment saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(rentEquipment)
                .post("/api/rent_equipments")
                .then()
                .statusCode(201)
                .extract().as(RentEquipment.class);
        RentEquipment got = given()
                .when().get("/api/rent_equipments/{rentEquipmentId}", saved.getId())
                .then()
                .statusCode(200)
                .extract().as(RentEquipment.class);
        assertThat(saved).isEqualTo(got);
        clearRentEquipmentAfterTest(saved.getId());
//        assertThat(saved.equals(got)).isTrue();
    }

    @Test
    public void post() {
        RentEquipment rentEquipment = createRentEquipment();
        RentEquipment saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(rentEquipment)
                .post("/api/rent_equipments")
                .then()
                .statusCode(201)
                .extract().as(RentEquipment.class);
        assertThat(saved.getId()).isNotNull();
        clearRentEquipmentAfterTest(saved.getId());
    }

    @Test
    public void postFailNoName() {
        RentEquipment rentEquipment = createRentEquipment();
        rentEquipment.setName(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(rentEquipment)
                .post("/api/rent_equipments")
                .then()
                .statusCode(400);
    }

    @Test
    public void put() {
        RentEquipment rentEquipment = createRentEquipment();
        RentEquipment saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(rentEquipment)
                .post("/api/rent_equipments")
                .then()
                .statusCode(201)
                .extract().as(RentEquipment.class);
        saved.setName("Updated");
        RentEquipment updated = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/rent_equipments")
                .then()
                .statusCode(200)
                .extract().as(RentEquipment.class);
        assertThat(updated.getName()).isEqualTo("Updated");
        clearRentEquipmentAfterTest(saved.getId());

    }

    @Test
    public void putFailNoPrice() {
        RentEquipment rentEquipment = createRentEquipment();
        RentEquipment saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(rentEquipment)
                .post("/api/rent_equipments")
                .then()
                .statusCode(201)
                .extract().as(RentEquipment.class);
        saved.setPrice(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/rent_equipments")
                .then()
                .statusCode(400);
        clearRentEquipmentAfterTest(saved.getId());
    }

    @Test
    public void delete() {
        RentEquipment rentEquipment = createRentEquipment();
        RentEquipment saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(rentEquipment)
                .post("/api/rent_equipments")
                .then()
                .statusCode(201)
                .extract().as(RentEquipment.class);
        given()
                .when()
                .delete("api/rent_equipments/{rentEquipmentId}", saved.getId())
                .then()
                .statusCode(204);
        //check if rentEquipment with that id is not found:
        given()
                .when().get("/api/rent_equipments/{rentEquipmentId}", saved.getId())
                .then()
                .statusCode(404);
    }

    @Test
    public void postFailDuplicateName() {
        RentEquipment rentEquipment = TestUtils.createRentEquipment();
        RentEquipment saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(rentEquipment)
                .post("/api/rent_equipments")
                .then()
                .extract().as(RentEquipment.class);

        RentEquipment rentEquipment2 = TestUtils.createRentEquipment();
        rentEquipment2.setName(rentEquipment.getName());

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(rentEquipment2)
                .post("/api/rent_equipments")
                .then()
                .statusCode(403)
                .extract().response();
        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(403);
        assertThat(actualExceptionMessage).isEqualTo("Rent equipment with given name already exists");

        clearRentEquipmentAfterTest(saved.getId());
    }

    @Test
    public void putFailEmptyId() {
        RentEquipment rentEquipment = TestUtils.createRentEquipment();
        RentEquipment saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(rentEquipment)
                .post("/api/rent_equipments")
                .then()
                .statusCode(201)
                .extract().as(RentEquipment.class);
        int id = saved.getId(); // we will need that to delete after test.
        saved.setId(null);
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/rent_equipments")
                .then()
                .statusCode(400)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(actualExceptionMessage).isEqualTo("Id for updating rent equipment cannot be null");

        clearRentEquipmentAfterTest(id);
    }

    @Test
    public void putFailNotFound() {
        RentEquipment rentEquipment = TestUtils.createRentEquipment();
        //there will never be id 0 in repo.
        rentEquipment.setId(0);
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(rentEquipment)
                .put("/api/rent_equipments")
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Rent equipment for given id not found!");
    }

    @Test
    public void deleteFailNotFound() {
        //there will never be id 0 in repo.
        Response response = given()
                .when()
                .delete("api/rent_equipments/0")
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Rent equipment for given id not found!");
    }

    private void clearRentEquipmentAfterTest(Integer id) {
        rentEquipmentService.deleteRentEquipmentById(id);
    }

}
