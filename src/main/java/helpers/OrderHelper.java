package helpers;

import common.EndPoints;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.ValidatableResponse;
import pojo.Ingredients;

import static io.restassured.RestAssured.given;

public class OrderHelper extends BaseHelper{
    @Step("Send POST request to /api/orders (with ingredients in body and authToken in headers)")
    public ValidatableResponse createOrderWithIngredientsAuth(String authToken, Ingredients ingredients) {
        return given()
                .filter(new AllureRestAssured())
                .spec(getBaseSpec())
                .and()
                .headers("Authorization", authToken)
                .body(ingredients)
                .post(EndPoints.ORDER)
                .then();
    }

    @Step("Send POST request to /api/orders (with authToken in headers)")
    public ValidatableResponse createOrderWithoutIngredientsAuth(String authToken) {
        return given()
                .filter(new AllureRestAssured())
                .spec(getBaseSpec())
                .and()
                .headers("Authorization", authToken)
                .post(EndPoints.ORDER)
                .then();
    }

    @Step("Send POST request to /api/orders (with ingredients in body)")
    public ValidatableResponse createOrderWithIngredientsNoAuth(Ingredients ingredients) {
        return given()
                .filter(new AllureRestAssured())
                .spec(getBaseSpec())
                .and()
                .body(ingredients)
                .post(EndPoints.ORDER)
                .then();
    }

    @Step("Send POST request to /api/orders")
    public ValidatableResponse createOrderWithoutIngredientsNoAuth() {
        return given()
                .filter(new AllureRestAssured())
                .spec(getBaseSpec())
                .and()
                .post(EndPoints.ORDER)
                .then();
    }

    @Step("Send GET request to /api/orders (with authToken in headers)")
    public ValidatableResponse getUserOrdersAuth(String authToken) {
        return given()
                .filter(new AllureRestAssured())
                .spec(getBaseSpec())
                .and()
                .headers("Authorization", authToken)
                .get(EndPoints.ORDER)
                .then();
    }

    @Step("Send GET request to /api/orders (without authToken in headers)")
    public ValidatableResponse getUserOrdersNoAuth() {
        return given()
                .filter(new AllureRestAssured())
                .spec(getBaseSpec())
                .and()
                .get(EndPoints.ORDER)
                .then();
    }
}