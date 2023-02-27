import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

public class ChangeUserTests {
    String accessToken = "";
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }
    String postfix = Integer.toString(new Random().nextInt(100000));
    String randomPassword = Integer.toString(new Random().nextInt(100000));

    @Test
    public void changeAuthUserTest() {
        User user = new User("email" + postfix + "@gmail.com", randomPassword, "name" + postfix);
        Response signUpResponse =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(user)
                        .when()
                        .post("/api/auth/register");
        accessToken = signUpResponse.jsonPath().getString("accessToken");
        String newPostfix = Integer.toString(new Random().nextInt(100000));
        User changedUser = new User ("email" + newPostfix + "@gmail.com", null, "name" + newPostfix);
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
                            .body(changedUser)
                            .when()
                            .patch("/api/auth/user");
        response.then().assertThat().body("success", is(true))
                .and()
                .statusCode(200);
    }

    @Test
    public void changeNotAuthUserTest() {
        User user = new User("email" + postfix + "@gmail.com", randomPassword, "name" + postfix);
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(user)
                        .when()
                        .post("/api/auth/register");
        String newPostfix = Integer.toString(new Random().nextInt(100000));
        User changedUser = new User ("email" + newPostfix + "@gmail.com", null, "name" + newPostfix);
        Response response =
                given()
                        .headers("Content-type", "application/json")
                        .and()
                        .body(changedUser)
                        .when()
                        .patch("/api/auth/user");
        response.then().assertThat().body("message", is("You should be authorised"))
                .and()
                .statusCode(401);
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
