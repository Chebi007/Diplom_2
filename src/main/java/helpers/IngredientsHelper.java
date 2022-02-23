package helpers;

import common.EndPoints;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class IngredientsHelper extends BaseHelper {
    @Step("Send GET request to /api/ingredients")
    public ValidatableResponse getIngredientsData() {
        return given()
                .filter(new AllureRestAssured())
                .spec(getBaseSpec())
                .get(EndPoints.INGREDIENTS_DATA)
                .then();
    }
}