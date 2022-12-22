import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

public class createUserTests {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }
    String postfix = Integer.toString(new Random().nextInt(100000));
    String randomPassword = Integer.toString(new Random().nextInt(100000));

    @Test
    public void CreateUserTest () {
        User user = new User("email" + postfix + "@gmail.com", randomPassword, "name" + postfix);
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(user)
                        .when()
                        .post("/api/auth/register");
        response.then().assertThat().body("success", is(true))
                .and()
                .statusCode(200);
    }

    @Test
    public void CreateSameUserTest () {
        User user = new User("email" + postfix + "@gmail.com", randomPassword, "name" + postfix);
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(user)
                        .when()
                        .post("/api/auth/register");
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(user)
                        .when()
                        .post("/api/auth/register");
        response.then().assertThat().body("message", is("User already exists"))
                .and()
                .statusCode(403);
    }

    @Test
    public void emptyPasswordCreationTest () {
        User user = new User("email" + postfix + "@gmail.com", null, "name" + postfix);
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(user)
                        .when()
                        .post("/api/auth/register");
        response.then().assertThat().body("message", is("Email, password and name are required fields"))
                .and()
                .statusCode(403);
    }
}
