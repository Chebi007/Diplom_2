import helpers.UserHelper;
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

import static data.UserDataGenerator.generateName;
import static data.UserDataGenerator.generatePassword;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class ChangeUserDataTests {
    private UserHelper userHelper;
    private String authToken;

    @Before
    @Step("setUp")
    public void setUp() {
        userHelper = new UserHelper();
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    @DisplayName("Change email, password and name for authorized user")
    public void testChangeUserDataAuthUser() {
        User user = User.getRandom();
        userHelper.registerUser(user);
        ValidatableResponse responseLoginUser = userHelper.loginUser(UserCredentials.getUserCredentials(user));
        authToken = responseLoginUser.extract().path("accessToken");

        userHelper.changeUserDataAuth(authToken, User.getRandom());
        ValidatableResponse responseUserData = userHelper.getUserData(authToken);

        assertThat(responseUserData.extract().statusCode(), equalTo(SC_OK));
        assertTrue(responseUserData.extract().path("success"));
        assertNotEquals("Email пользователя одинаковый",
                        user.email, responseUserData.extract().path("user.email"));
        assertNotEquals("Имя пользователя одинаковое",
                        user.name, responseUserData.extract().path("user.name"));
    }

    @Test
    @DisplayName("Change email, password and name for unauthorized user")
    public void testChangeUserDataUnAuthUser() {
        String wrongAuthToken = "1213254523s3eg1D*";
        ValidatableResponse responseChangeUserData = userHelper.changeUserDataAuth(wrongAuthToken, User.getRandom());

        assertThat(responseChangeUserData.extract().statusCode(), equalTo(SC_UNAUTHORIZED));
        assertFalse(responseChangeUserData.extract().path("success"));
        assertEquals("Фактическое сообщение в ответе отличается от ожидаемого",
                    "You should be authorised", responseChangeUserData.extract().path("message"));
    }

    @Test
    @DisplayName("Change user email for authorized user")
    public void testChangeUserEmailAuthUser() {
        User user = User.getRandom();
        userHelper.registerUser(user);
        ValidatableResponse responseLoginUser = userHelper.loginUser(UserCredentials.getUserCredentials(user));
        authToken = responseLoginUser.extract().path("accessToken");

        userHelper.changeUserDataAuth(authToken, User.getRandomWithEmail());
        ValidatableResponse responseUserData = userHelper.getUserData(authToken);

        assertThat(responseUserData.extract().statusCode(), equalTo(SC_OK));
        assertTrue(responseUserData.extract().path("success"));
        assertNotEquals("Email пользователя не изменился",
                        user.email, responseUserData.extract().path("user.email"));
        assertEquals(user.name, responseUserData.extract().path("user.name"));
    }

    @Test
    @DisplayName("Change user name for authorized user")
    public void testChangeUserNameAuthUser() {
        User user = User.getRandom();
        userHelper.registerUser(user);
        ValidatableResponse responseLoginUser = userHelper.loginUser(UserCredentials.getUserCredentials(user));
        authToken = responseLoginUser.extract().path("accessToken");

        userHelper.changeUserDataAuth(authToken, new User(user.email, generateName()));
        ValidatableResponse responseUserData = userHelper.getUserData(authToken);

        assertThat(responseUserData.extract().statusCode(), equalTo(SC_OK));
        assertTrue(responseUserData.extract().path("success"));
        assertEquals(user.email, responseUserData.extract().path("user.email"));
        assertNotEquals("Имя пользователя не изменилось",
                user.name, responseUserData.extract().path("user.name"));
    }

    @Test
    @DisplayName("Change user password for authorized user")
    public void testChangeUserPasswordAuthUser() {
        User user = User.getRandom();
        userHelper.registerUser(user);
        ValidatableResponse responseLoginUser = userHelper.loginUser(UserCredentials.getUserCredentials(user));
        authToken = responseLoginUser.extract().path("accessToken");

        userHelper.changeUserDataAuth(authToken, new User(user.email, generatePassword(), user.name));
        ValidatableResponse responseUserData = userHelper.getUserData(authToken);

        assertThat(responseUserData.extract().statusCode(), equalTo(SC_OK));
        assertTrue(responseUserData.extract().path("success"));
        assertEquals(user.email, responseUserData.extract().path("user.email"));
        assertEquals(user.name, responseUserData.extract().path("user.name"));

        ValidatableResponse responseLoginUserAfter = userHelper.loginUser(UserCredentials.getUserCredentials(user));
        assertThat(responseLoginUserAfter.extract().statusCode(), equalTo(SC_UNAUTHORIZED));
        assertEquals("email or password are incorrect", responseLoginUserAfter.extract().path("message"));
    }

    @After
    @Step("Delete user")
    public void tearDown() {
        if (authToken != null) {
            userHelper.deleteUser(authToken);
        }
    }
}