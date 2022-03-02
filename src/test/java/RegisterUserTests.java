import helpers.UserHelper;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojo.User;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class RegisterUserTests {
    private UserHelper userHelper;
    private String authToken;

    @Before
    @Step("setUp")
    public void setUp() {
        userHelper = new UserHelper();
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    @DisplayName("Register user with email, password and name")
    @Description("This test check user registration when all fields are filled out")
    public void testRegisterUserEmailPasswordName() {
        User user = User.getRandom();
        ValidatableResponse response = userHelper.registerUser(user);

        authToken = response.extract().path("accessToken");

        assertThat(response.extract().statusCode(), equalTo(SC_OK));
        assertTrue(response.extract().path("success"));
        assertEquals("Email пользователя отличается", user.email, response.extract().path("user.email"));
        assertEquals("Name пользователя отличается", user.name, response.extract().path("user.name"));
    }

    @Test
    @DisplayName("Register user with only email")
    @Description("This test check user registration when only email is filled")
    public void testRegisterUserEmailOnly() {
        User user = User.getRandomWithEmail();
        ValidatableResponse response = userHelper.registerUser(user);

        assertThat(response.extract().statusCode(), equalTo(SC_FORBIDDEN));
        assertFalse(response.extract().path("success"));
        assertEquals("Фактическое сообщение в ответе отличается от ожидаемого",
                "Email, password and name are required fields", response.extract().path("message"));
    }

    @Test
    @DisplayName("Register user with the same email")
    @Description("This test check user registration when user already exists")
    public void testRegisterUserSameEmail() {
        User user = User.getRandom();
        userHelper.registerUser(user);
        ValidatableResponse response = userHelper.registerUser(user);

        assertThat(response.extract().statusCode(), equalTo(SC_FORBIDDEN));
        assertFalse(response.extract().path("success"));
        assertEquals("Фактическое сообщение в ответе отличается от ожидаемого", "User already exists",
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