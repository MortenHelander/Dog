package app.controllers;

import app.config.ApplicationConfig;
import app.daos.DogDAO;
import app.entities.Dog;
import app.entities.FakeDog;
import app.enums.Breed;
import app.populators.DogPopulator;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class DogControllerTest {

    private static Javalin app;
    private static DogDAO dogDAO = new DogDAO();
    private static DogController dogController;
    private static Map<Long, Dog> dogMap;

    @BeforeAll
    static void init() {
        dogController = new DogController(dogDAO);
        app = ApplicationConfig.startServer(7070, dogDAO);
        RestAssured.baseURI = "http://localhost:7070/dogs";
    }

    @BeforeEach
    void setUp() {
        dogMap = DogPopulator.populate(dogDAO);
    }

    @AfterEach
    void tearDown() {
        dogDAO.clear();
    }

    @AfterAll
    static void shutDown() {
        ApplicationConfig.stopServer(app);
    }

    @Test
    void getAll() {
        List<Dog> expected = dogDAO.getAll();
        List<Dog> actual = given()
                .when()
                .get("/")
                .then()
                .statusCode(200)
                .log().all()
                .body("size()", is(4))
                .body("name", containsInAnyOrder("King", "Mini", "Boss", "Osvald"))
                .extract()
                .jsonPath()
                .getList("", Dog.class);

        assertThat(actual, containsInAnyOrder(expected.toArray(new Dog[0])));
    }

    @Test
    void getByIdSuccess() {

        Dog expected = dogDAO.getById(1).orElseThrow(() -> new IllegalArgumentException("Dog with id " + 1 + " not found"));

        Dog actual = given()
                .when()
                .get("/1")
                .then()
                .statusCode(200)
                .log().all()
                .body("id", is(1))
                .body("name", is("King"))
                .body("breed", is("LABRADOR"))
                .extract()
                .jsonPath()
                .getObject("", Dog.class);

        assertThat(actual.getId(), is(expected.getId()));

    }

    @Test
    void getByIdSad() {

        given()
                .when()
                .get("999")
                .then()
                .statusCode(400)
                .log().all()
                .body(is("Dog with id 999 not found"));
    }

    @Test
    void createSuccess() {

        Dog newDog = new Dog("Fido", Breed.BULLDOG);

        given()
                .when()
                .contentType(ContentType.JSON)
                .body(newDog)
                .when()
                .post("/")
                .then()
                .statusCode(200)
                .log().all();

        given()
                .when()
                .get("/")
                .then()
                .statusCode(200)
                .log().all()
                .body("size()", is(6))
                .body("name", containsInAnyOrder("King", "Mini", "Boss", "Osvald", "Fido"))
                .extract()
                .jsonPath()
                .getList("", Dog.class);
    }

//    @Test
//    void createSad() {
//
//        FakeDog nullDog = new FakeDog("fake-name", "andreas", -99);
//
//        given()
//                .when()
//                .contentType(ContentType.JSON)
//                .body(nullDog)
//                .when()
//                .post("/")
//                .then()
//                .statusCode(200)
//                .log().all();
//
//        given()
//                .when()
//                .get("/")
//                .then()
//                .statusCode(200)
//                .log().all()
//                .body("size()", is(4))
//                .body("name", containsInAnyOrder("King", "Mini", "Boss", "Osvald"))
//                .extract()
//                .jsonPath()
//                .getList("", Dog.class);
//
//        List<Dog> dogs = dogDAO.getAll();
//        System.out.println(dogs);
//    }
}