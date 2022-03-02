import helpers.IngredientsHelper;
import helpers.OrderHelper;
import helpers.UserHelper;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pojo.Ingredients;
import pojo.User;
import pojo.UserCredentials;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class GetUserOrdersTests {
    private IngredientsHelper ingredientsHelper;
    private OrderHelper orderHelper;
    private UserHelper userHelper;
    private String authToken;

    @Before
    @Step("setUp")
    public void setUp () {
        ingredientsHelper = new IngredientsHelper();
        orderHelper = new OrderHelper();
        userHelper = new UserHelper();
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    @DisplayName("Get user orders for authorized user")
    public void testGetUserOrdersAuth() {
        User user = User.getRandom();
        userHelper.registerUser(user);
        ValidatableResponse responseLoginUser = userHelper.loginUser(UserCredentials.getUserCredentials(user));
        authToken = responseLoginUser.extract().path("accessToken");

        ValidatableResponse responseGetIngredientsData = ingredientsHelper.getIngredientsData();
        List<String> ingredients = responseGetIngredientsData.extract().body().jsonPath().get("data._id");

        List<String> ingredientsList1 = new ArrayList<>();
        ingredientsList1.add(ingredients.get(0));
        List<String> ingredientsList2 = new ArrayList<>();
        ingredientsList2.add(ingredients.get(1));
        List<String> ingredientsList3 = new ArrayList<>();
        ingredientsList3.add(ingredients.get(2));

        orderHelper.createOrderWithIngredientsAuth(authToken, new Ingredients(ingredientsList1));
        orderHelper.createOrderWithIngredientsAuth(authToken, new Ingredients(ingredientsList2));
        orderHelper.createOrderWithIngredientsAuth(authToken, new Ingredients(ingredientsList3));

        ValidatableResponse response = orderHelper.getUserOrdersAuth(authToken);

        assertThat(response.extract().statusCode(), equalTo(SC_OK));
        Assert.assertNotNull(response.extract().path("total"));
        Assert.assertNotNull(response.extract().path("totalToday"));
    }

    @Test
    @DisplayName("Get user orders for unauthorized user")
    public void testGetUserOrdersNoAuth() {
        User user = User.getRandom();
        userHelper.registerUser(user);
        ValidatableResponse responseLoginUser = userHelper.loginUser(UserCredentials.getUserCredentials(user));
        authToken = responseLoginUser.extract().path("accessToken");

        ValidatableResponse responseGetIngredientsData = ingredientsHelper.getIngredientsData();
        List<String> ingredients = responseGetIngredientsData.extract().body().jsonPath().get("data._id");

        List<String> ingredientsList1 = new ArrayList<>();
        ingredientsList1.add(ingredients.get(0));
        List<String> ingredientsList2 = new ArrayList<>();
        ingredientsList2.add(ingredients.get(1));

        orderHelper.createOrderWithIngredientsAuth(authToken, new Ingredients(ingredientsList1));
        orderHelper.createOrderWithIngredientsAuth(authToken, new Ingredients(ingredientsList2));

        ValidatableResponse response = orderHelper.getUserOrdersNoAuth();

        assertThat(response.extract().statusCode(), equalTo(SC_UNAUTHORIZED));
        assertFalse(response.extract().path("success"));
        assertEquals("Фактическое сообщение в ответе отличается от ожидаемого",
                "You should be authorised", response.extract().path("message"));
    }

    @After
    @Step("Delete user")
    public void tearDown() {
        if (authToken != null) {
            userHelper.deleteUser(authToken);
        }
    }
}
