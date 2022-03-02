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

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class CreateOrderTests {
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
    @DisplayName("Create order with ingredients for authorized user")
    public void testCreateOrderWithIngredientsAuth() {
        User user = User.getRandom();
        userHelper.registerUser(user);
        ValidatableResponse responseLoginUser = userHelper.loginUser(UserCredentials.getUserCredentials(user));
        authToken = responseLoginUser.extract().path("accessToken");

        ValidatableResponse responseGetIngredientsData = ingredientsHelper.getIngredientsData();
        List<String> ingredients = responseGetIngredientsData.extract().body().jsonPath().get("data._id");

        List<String> ingredientsList = new ArrayList<>();
        ingredientsList.add(ingredients.get(0));
        ingredientsList.add(ingredients.get(1));

        ValidatableResponse createOrder = orderHelper.createOrderWithIngredientsAuth(authToken, new Ingredients(ingredientsList));
        assertThat(createOrder.extract().statusCode(), equalTo(SC_OK));
        Assert.assertNotNull(createOrder.extract().path("order._id"));
        Assert.assertNotNull(createOrder.extract().path("order.number"));
    }

    @Test
    @DisplayName("Create order without ingredients for authorized user")
    public void testCreateOrderAuthWithoutIngredientsAuth() {
        User user = User.getRandom();
        userHelper.registerUser(user);
        ValidatableResponse responseLoginUser = userHelper.loginUser(UserCredentials.getUserCredentials(user));
        authToken = responseLoginUser.extract().path("accessToken");

        ValidatableResponse response = orderHelper.createOrderWithoutIngredientsAuth(authToken);
        assertThat(response.extract().statusCode(), equalTo(SC_BAD_REQUEST));
        assertFalse(response.extract().path("success"));
        assertEquals("Фактическое сообщение в ответе отличается от ожидаемого", "Ingredient ids must be provided",
                response.extract().path("message"));
    }

    @Test
    @DisplayName("Create order with ingredients for unauthorized user")
    public void testCreateOrderWithIngredientsNoAuth() {
        ValidatableResponse responseGetIngredientsData = ingredientsHelper.getIngredientsData();
        List<String> ingredients = responseGetIngredientsData.extract().body().jsonPath().get("data._id");

        List<String> ingredientsList = new ArrayList<>();
        ingredientsList.add(ingredients.get(0));
        ingredientsList.add(ingredients.get(1));

        ValidatableResponse response = orderHelper.createOrderWithIngredientsNoAuth(new Ingredients(ingredientsList));
        assertThat(response.extract().statusCode(), equalTo(SC_OK));
        assertTrue(response.extract().path("success"));
        Assert.assertNotNull(response.extract().path("name"));
        Assert.assertNotNull(response.extract().path("order.number"));
    }

    @Test
    @DisplayName("Create order without ingredients for unauthorized user")
    public void testCreateOrderWithoutIngredientsNoAuth() {
        ValidatableResponse response = orderHelper.createOrderWithoutIngredientsNoAuth();
        assertThat(response.extract().statusCode(), equalTo(SC_BAD_REQUEST));
        assertFalse(response.extract().path("success"));
        assertEquals("Фактическое сообщение в ответе отличается от ожидаемого",
                "Ingredient ids must be provided", response.extract().path("message"));
    }

    @Test
    @DisplayName("Create order with invalid ingredients for authorized user")
    public void testCreateOrderWithInvalidIngredientsAuth() {
        User user = User.getRandom();
        userHelper.registerUser(user);
        ValidatableResponse responseLoginUser = userHelper.loginUser(UserCredentials.getUserCredentials(user));
        authToken = responseLoginUser.extract().path("accessToken");

        List<String> ingredientsList = new ArrayList<>();
        ingredientsList.add("99c1c0071d1f82001bda0000");

        ValidatableResponse response = orderHelper.createOrderWithIngredientsAuth(authToken, new Ingredients(ingredientsList));
        assertThat(response.extract().statusCode(), equalTo(SC_BAD_REQUEST));
        assertFalse(response.extract().path("success"));
        assertEquals("Фактическое сообщение в ответе отличается от ожидаемого", "One or more ids provided are incorrect",
                response.extract().path("message"));
    }

    @After
    @Step("Delete user")
    public void tearDown() {
        if (authToken != null) {
            userHelper.deleteUser(authToken);
        }
    }
}
