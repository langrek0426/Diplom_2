import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

public class signInTests {
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }
    String postfix = Integer.toString(new Random().nextInt(100000));
    String randomPassword = Integer.toString(new Random().nextInt(100000));

    @Test
    public void signInTest () {
        User user = new User("email" + postfix + "@gmail.com", randomPassword, "name" + postfix);
        Response signUpResponse =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(user)
                        .when()
                        .post("/api/auth/register");
        String accessToken = signUpResponse.jsonPath().getString("accessToken");

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .auth().oauth2(accessToken)
                        .and()
                        .body(user)
                        .when()
                        .post("/api/auth/login");
        response.then().assertThat().body("success", is(true))
                .and()
                .statusCode(200);
    }

    @Test
    public void failedSignInTest () {
        User user = new User("email" + postfix + "@gmail.com", randomPassword, "name" + postfix);
        Response signUpResponse =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(user)
                        .when()
                        .post("/api/auth/register");
        String accessToken = signUpResponse.jsonPath().getString("accessToken");
        user.setPassword("wrong password");
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .auth().oauth2(accessToken)
                        .and()
                        .body(user)
                        .when()
                        .post("/api/auth/login");
        response.then().assertThat().body("message", is("email or password are incorrect"))
                .and()
                .statusCode(401);
    }
}
