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
import pojo.UserCredentials;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;


public class LoginUserTests {
    private UserHelper userHelper;
    private User user;
    private String authToken;

    @Before
    @Step("setUp")
    public void setUp() {
        userHelper = new UserHelper();
        user = User.getRandom();
        ValidatableResponse responseRegisterUser = userHelper.registerUser(user);
        authToken = responseRegisterUser.extract().path("accessToken");
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    @DisplayName("Login user with correct credentials")
    @Description("Check login with correct email and password")
    public void testLoginUserCorrectCredentials() {
        ValidatableResponse responseLoginUser = userHelper.loginUser(UserCredentials.getUserCredentials(user));

        assertThat(responseLoginUser.extract().statusCode(), equalTo(SC_OK));
        assertTrue(responseLoginUser.extract().path("success"));
        assertEquals("Email пользователя отличается", user.email,
                    responseLoginUser.extract().path("user.email"));
        assertEquals("Name пользователя отличается", user.name,
                    responseLoginUser.extract().path("user.name"));
    }

    @Test
    @DisplayName("Login user with wrong email")
    public void testLoginUserWrongEmail() {
        String wrongEmail = "wrong_email@mail.ru";
        ValidatableResponse responseLoginUser = userHelper.loginUser(new UserCredentials(wrongEmail, user.password));

        assertThat(responseLoginUser.extract().statusCode(), equalTo(SC_UNAUTHORIZED));
        assertFalse(responseLoginUser.extract().path("success"));
        assertEquals("Фактическое сообщение в ответе отличается от ожидаемого",
                "email or password are incorrect", responseLoginUser.extract().path("message"));
    }

    @Test
    @DisplayName("Login user with wrong password")
    public void testLoginUserWrongPassword() {
        String wrongPassword = "1213254523s3eg1D*";
        ValidatableResponse responseLoginUser = userHelper.loginUser(new UserCredentials(user.email, wrongPassword));

        assertThat(responseLoginUser.extract().statusCode(), equalTo(SC_UNAUTHORIZED));
        assertFalse(responseLoginUser.extract().path("success"));
        assertEquals("Фактическое сообщение в ответе отличается от ожидаемого",
                "email or password are incorrect", responseLoginUser.extract().path("message"));
    }

    @After
    @Step("Delete user")
    public void tearDown() {
        if (authToken != null) {
            userHelper.deleteUser(authToken);
        }
    }
}