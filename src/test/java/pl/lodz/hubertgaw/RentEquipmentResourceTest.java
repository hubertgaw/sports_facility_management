package pl.lodz.hubertgaw;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import pl.lodz.hubertgaw.dto.RentEquipment;
import pl.lodz.hubertgaw.service.RentEquipmentService;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

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
                .statusCode(200);
        //check if rentEquipment with that id is not found:
        given()
                .when().get("/api/rent_equipments/{rentEquipmentId}", saved.getId())
                .then()
                .statusCode(404);
    }

    private RentEquipment createRentEquipment() {
        RentEquipment rentEquipment = new RentEquipment();
        rentEquipment.setName(RandomStringUtils.randomAlphabetic(10));
        rentEquipment.setPrice(Double.valueOf(RandomStringUtils.randomNumeric(2)));
        return rentEquipment;
    }

    private void clearRentEquipmentAfterTest(Integer id) {
        rentEquipmentService.deleteRentEquipmentById(id);
    }

}
