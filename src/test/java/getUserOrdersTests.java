import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class getUserOrdersTests {
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }
    String postfix = Integer.toString(new Random().nextInt(100000));
    String randomPassword = Integer.toString(new Random().nextInt(100000));
    ArrayList<String> ingredients = new ArrayList<>();
    User user = new User("email" + postfix + "@gmail.com", randomPassword, "name" + postfix);

    @Test
    public void authUserTest () {
        Response signUpResponse =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(user)
                        .when()
                        .post("/api/auth/register");
        String accessToken = signUpResponse.jsonPath().getString("accessToken");
        given()
                .header("Content-type", "application/json")
                .auth().oauth2(accessToken)
                .and()
                .body(user)
                .when()
                .post("/api/auth/login");


        ingredients.add("61c0c5a71d1f82001bdaaa6f");

        Order order = new Order (ingredients);
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(order)
                        .when()
                        .post("/api/orders");
        Response response =
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
                    .get("/api/orders");
            response.then().assertThat().body("orders", is(notNullValue()))
                    .and()
                    .statusCode(200);
    }

    @Test
    public void notAuthUserTest () {
        Response response =
                given()
                        .headers("Content-type", "application/json")
                        .and()
                        .when()
                        .get("/api/orders");
        response.then().assertThat().body("message", is("You should be authorised"))
                .and()
                .statusCode(401);
    }
}
