package pl.lodz.hubertgaw;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import pl.lodz.hubertgaw.dto.CustomObject;
import pl.lodz.hubertgaw.dto.RentEquipment;
import pl.lodz.hubertgaw.service.RentEquipmentService;
import pl.lodz.hubertgaw.service.SportObjectService;
import pl.lodz.hubertgaw.utils.TestUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static pl.lodz.hubertgaw.utils.TestUtils.createCustomObject;

@QuarkusTest
@TestSecurity(authorizationEnabled = false)
public class CustomObjectResourceTest {


    @Inject
    SportObjectService sportObjectService;
    @Inject
    RentEquipmentService rentEquipmentService;

    @Test
    public void getAll() {
        given()
                .when().get("/api/custom_objects")
                .then()
                .statusCode(200);
    }

    @Test
    public void getById() {
        CustomObject customObject = createCustomObject();
        CustomObject saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(customObject)
                .post("/api/custom_objects")
                .then()
                .statusCode(201)
                .extract().as(CustomObject.class);
        CustomObject got = given()
                .when().get("/api/custom_objects/{customObjectId}", saved.getId())
                .then()
                .statusCode(200)
                .extract().as(CustomObject.class);
        assertThat(saved).isEqualTo(got);
        TestUtils.clearSportObjectAfterTest(saved.getId());
//        assertThat(saved.equals(got)).isTrue();
    }

    @Test
    public void getByIdFailNotFound() {
        Response response = given()
                .when().get("/api/custom_objects/{customObjectId}", 0)// there will be for sure no object with id 0
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Custom object for given id not found. Try to search in all sport objects or change the id.");

    }

    @Test
    public void getByType() {
        CustomObject customObject1 = createCustomObject();
        CustomObject customObject2 = createCustomObject();
        CustomObject customObject3 = createCustomObject();

        customObject1.setType("taka_sama");
        customObject2.setType("taka_sama");
        customObject3.setType("inna");

        CustomObject saved1 = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(customObject1)
                .post("/api/custom_objects")
                .then()
                .statusCode(201)
                .extract().as(CustomObject.class);
        CustomObject saved2 = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(customObject2)
                .post("/api/custom_objects")
                .then()
                .statusCode(201)
                .extract().as(CustomObject.class);
        CustomObject saved3 = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(customObject3)
                .post("/api/custom_objects")
                .then()
                .statusCode(201)
                .extract().as(CustomObject.class);


        List<CustomObject> got = given()
                .when().get("/api/custom_objects/type/{customObjectType}", "taka_sama")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath().getList(".", CustomObject.class);
        assertThat(got.size()).isEqualTo(2);
        assertThat(got.get(0)).isEqualTo(saved1);
        assertThat(got.get(1)).isEqualTo(saved2);
        assertThat(got.contains(saved3)).isFalse();

        TestUtils.clearSportObjectAfterTest(saved1.getId());
        TestUtils.clearSportObjectAfterTest(saved2.getId());
        TestUtils.clearSportObjectAfterTest(saved3.getId());
    }

    @Test
    public void getByTypeTypeNotFound() {
        CustomObject customObject1 = createCustomObject();

        customObject1.setType("typ");

        CustomObject saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(customObject1)
                .post("/api/custom_objects")
                .then()
                .statusCode(201)
                .extract().as(CustomObject.class);

        Response response = given()
                .when().get("/api/custom_objects/type/{customObjectType}", "inny_typ")
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("No custom object was found for provided type");

        TestUtils.clearSportObjectAfterTest(saved.getId());
    }

    @Test
    public void post() {
        CustomObject customObject = createCustomObject();
        CustomObject saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(customObject)
                .post("/api/custom_objects")
                .then()
                .statusCode(201)
                .extract().as(CustomObject.class);
        assertThat(saved.getId()).isNotNull();
        TestUtils.clearSportObjectAfterTest(saved.getId());
    }

    @Test
    public void postFailNoName() {
        CustomObject customObject = createCustomObject();
        customObject.setName(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(customObject)
                .post("/api/custom_objects")
                .then()
                .statusCode(400);
    }

    @Test
    public void postFailWrongType() {
        CustomObject customObject = createCustomObject();
        customObject.setType("nazwa ze spacja");

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(customObject)
                .post("/api/custom_objects")
                .then()
                .statusCode(400)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(actualExceptionMessage).isEqualTo("Type can contains only letters and digits (no space allowed)");

    }

    @Test
    public void put() {
        CustomObject customObject = createCustomObject();
        CustomObject saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(customObject)
                .post("/api/custom_objects")
                .then()
                .statusCode(201)
                .extract().as(CustomObject.class);
        saved.setName("Updated");
        CustomObject updated = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/custom_objects")
                .then()
                .statusCode(200)
                .extract().as(CustomObject.class);
        assertThat(updated.getName()).isEqualTo("Updated");
        TestUtils.clearSportObjectAfterTest(saved.getId());

    }

    @Test
    public void putFailNoType() {
        CustomObject customObject = createCustomObject();
        CustomObject saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(customObject)
                .post("/api/custom_objects")
                .then()
                .statusCode(201)
                .extract().as(CustomObject.class);
        saved.setType(null);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/custom_objects")
                .then()
                .statusCode(400);
        TestUtils.clearSportObjectAfterTest(saved.getId());
    }

    @Test
    public void postFailDuplicateName() {
        CustomObject customObject = TestUtils.createCustomObject();
        CustomObject saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(customObject)
                .post("/api/custom_objects")
                .then()
                .extract().as(CustomObject.class);

        CustomObject customObject2 = TestUtils.createCustomObject();
        customObject2.setName(customObject.getName());

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(customObject2)
                .post("/api/custom_objects")
                .then()
                .statusCode(403)
                .extract().response();
        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(403);
        assertThat(actualExceptionMessage).isEqualTo("Sport object with given name already exists");

        TestUtils.clearSportObjectAfterTest(saved.getId());
    }

    @Test
    public void putFailEmptyId() {
        CustomObject customObject = TestUtils.createCustomObject();
        CustomObject saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(customObject)
                .post("/api/custom_objects")
                .then()
                .statusCode(201)
                .extract().as(CustomObject.class);
        int id = saved.getId(); // we will need that to delete after test.
        saved.setId(null);
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .put("/api/custom_objects")
                .then()
                .statusCode(400)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(actualExceptionMessage).isEqualTo("Id for updating custom object cannot be null");

        TestUtils.clearSportObjectAfterTest(id);
    }

    @Test
    public void putFailNotFound() {
        CustomObject customObject = TestUtils.createCustomObject();
        //there will never be id 0 in repo.
        customObject.setId(0);
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(customObject)
                .put("/api/custom_objects")
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Custom object for given id not found. Try to search in all sport objects or change the id.");
    }

    @Test
    public void putFailWrongTypeFormat() {
        CustomObject customObject = TestUtils.createCustomObject();
        CustomObject saved = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(customObject)
                .post("/api/custom_objects")
                .then()
                .statusCode(201)
                .extract().as(CustomObject.class);
        saved.setType("nazwa ze spacja");
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(saved)
                .body(saved)
                .put("/api/custom_objects")
                .then()
                .statusCode(400)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(actualExceptionMessage).isEqualTo("Type can contains only letters and digits (no space allowed)");

        TestUtils.clearSportObjectAfterTest(saved.getId());
    }


    @Test
    public void putEquipmentToCustomObject() {
        CustomObject customObject = TestUtils.createCustomObject();
        RentEquipment rentEquipment = TestUtils.createRentEquipment();

        CustomObject savedTrack = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(customObject)
                .post("/api/custom_objects")
                .then()
                .statusCode(201)
                .extract().as(CustomObject.class);

        RentEquipment savedEquipment = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(rentEquipment)
                .post("/api/rent_equipments")
                .then()
                .statusCode(201)
                .extract().as(RentEquipment.class);

        CustomObject trackAfterPut = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(customObject)
                .put("/api/custom_objects/{customObjectId}/rent_equipment/{rentEquipmentId}",
                        savedTrack.getId(), savedEquipment.getId())
                .then()
                .statusCode(200)
                .extract().as(CustomObject.class);

        //compare name of rent equipment
        assertThat(new ArrayList<>(trackAfterPut.getRentEquipmentNames()).get(0)).isEqualTo(savedEquipment.getName());

        TestUtils.clearRentEquipmentAfterTest(savedEquipment.getId());
        TestUtils.clearSportObjectAfterTest(savedTrack.getId());
    }

    @Test
    public void putEquipmentToCustomObjectCustomObjectNotFound() {
        CustomObject customObject = TestUtils.createCustomObject();
        RentEquipment rentEquipment = TestUtils.createRentEquipment();

        RentEquipment savedEquipment = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(rentEquipment)
                .post("/api/rent_equipments")
                .then()
                .statusCode(201)
                .extract().as(RentEquipment.class);

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(customObject)
                .put("/api/custom_objects/0/rent_equipment/{rentEquipmentId}",
                        savedEquipment.getId())
                .then()
                .statusCode(404)
                .extract().response();

        //compare name of rent equipment
//        assertThat(new ArrayList<>(trackAfterPut.getRentEquipmentNames()).get(0)).isEqualTo(savedEquipment.getName());

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Custom object for given id not found. Try to search in all sport objects or change the id.");


        TestUtils.clearRentEquipmentAfterTest(savedEquipment.getId());
//        TestUtils.clearSportObjectAfterTest(savedTrack.getId());
    }

    @Test
    public void putEquipmentToCustomObjectRentEquipmentNotFound() {
        CustomObject customObject = TestUtils.createCustomObject();

        CustomObject savedTrack = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(customObject)
                .post("/api/custom_objects")
                .then()
                .statusCode(201)
                .extract().as(CustomObject.class);

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(customObject)
                .put("/api/custom_objects/{customObjectId}/rent_equipment/0",
                        savedTrack.getId())
                .then()
                .statusCode(404)
                .extract().response();

        String responseMessage = response.getBody().asString();
        String actualExceptionMessage = TestUtils.getActualExceptionMessage(responseMessage);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(actualExceptionMessage).isEqualTo("Rent equipment for given id not found!");

        TestUtils.clearSportObjectAfterTest(savedTrack.getId());
    }

}
