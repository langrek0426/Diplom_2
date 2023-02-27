import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

public class MakeOrderTests {
    String accessToken = "";
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }
    String postfix = Integer.toString(new Random().nextInt(100000));
    String randomPassword = Integer.toString(new Random().nextInt(100000));
    ArrayList<String> ingredients = new ArrayList<>();
    User user = new User("email" + postfix + "@gmail.com", randomPassword, "name" + postfix);

    @Test
    public void authUserTest() {
    Response signUpResponse =
            given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post("/api/auth/register");
    accessToken = signUpResponse.jsonPath().getString("accessToken");
            given()
                .header("Content-type", "application/json")
                .auth().oauth2(accessToken)
                .and()
                .body(user)
                .when()
                .post("/api/auth/login");


    ingredients.add("61c0c5a71d1f82001bdaaa6f");

    Order order = new Order (ingredients);
    Response response =
            given()
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .when()
                .post("/api/orders");
    response.then().assertThat().body("success", is(true))
            .and()
            .statusCode(200);
    }

    @Test
    public void notAuthUserTest() {
        ingredients.add("61c0c5a71d1f82001bdaaa6f");
        Order order = new Order (ingredients);
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(order)
                        .when()
                        .post("/api/orders");
        response.then().assertThat().body("success", is(true))
                .and()
                .statusCode(200);
    }

    @Test
    public void emptyIngredientsTest() {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .when()
                        .post("/api/orders");
        response.then().assertThat().body("message", is("Ingredient ids must be provided"))
                .and()
                .statusCode(400);
    }

    @Test
    public void wrongIngredientsTest() {
        ingredients.add("wrong hash");
        Order order = new Order (ingredients);
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(order)
                        .when()
                        .post("/api/orders");
        response.then().assertThat().statusCode(500);
    }
    @After
    public void deleteData() {
        if (!accessToken.isEmpty()) {
            given()
                    .headers(
                            "Authorization",
                            accessToken,
                            "Content-Type",
                            ContentType.JSON,
                            "Accept",
                            ContentType.JSON)
                    .and()
                    .when()
                    .delete("/api/auth/user");
        }
    }
}
