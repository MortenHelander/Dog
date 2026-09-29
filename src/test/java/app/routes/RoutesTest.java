package app.routes;

import app.config.ApplicationConfig;
import app.daos.DogDAO;
import app.entities.Dog;
import app.populators.DogPopulator;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

class RoutesTest {

    private static Javalin app;
    private static DogDAO dogDAO = new DogDAO();
    private static Map<Long, Dog> dogMap;

    @BeforeAll
    static void init(){
        app = ApplicationConfig.startServer(7070, dogDAO);
        RestAssured.baseURI =  "http://localhost:7070/dogs";
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
    static void shutDown(){
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
}