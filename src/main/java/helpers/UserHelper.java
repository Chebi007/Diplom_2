package helpers;

import common.EndPoints;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.ValidatableResponse;
import pojo.User;
import pojo.UserCredentials;

import static io.restassured.RestAssured.given;

public class UserHelper extends BaseHelper {
    @Step("Send POST request to /api/auth/register")
    public ValidatableResponse registerUser(User user) {
        return given()
                .filter(new AllureRestAssured())
                .spec(getBaseSpec())
                .and()
                .body(user)
                .when()
                .post(EndPoints.REGISTER_USER)
                .then();
    }

    @Step("Send POST request to api/auth/login")
    public ValidatableResponse loginUser(UserCredentials credentials) {
        return given()
                .filter(new AllureRestAssured())
                .spec(getBaseSpec())
                .and()
                .body(credentials)
                .when()
                .post(EndPoints.LOGIN_USER)
                .then();
    }

    @Step("Send GET request to api/auth/user")
    public ValidatableResponse getUserData (String authToken) {
        return given()
                .filter(new AllureRestAssured())
                .spec(getBaseSpec())
                .and()
                .headers("Authorization", authToken)
                .get(EndPoints.USER)
                .then();
    }

    @Step("Send PATCH request to api/auth/user")
    public ValidatableResponse changeUserDataAuth(String authToken, User user) {
        return given()
                .filter(new AllureRestAssured())
                .spec(getBaseSpec())
                .and()
                .headers("Authorization", authToken)
                .body(user)
                .patch(EndPoints.USER)
                .then();
    }

    @Step("Send DELETE request to api/auth/user")
    public ValidatableResponse deleteUser (String authToken) {
        return given()
                .filter(new AllureRestAssured())
                .spec(getBaseSpec())
                .and()
                .headers("Authorization", authToken)
                .delete(EndPoints.USER)
                .then();
    }
}