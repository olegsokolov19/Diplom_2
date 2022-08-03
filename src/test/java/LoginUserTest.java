import Model.UserModel;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class LoginUserTest {
    private User user;
    String token;

    @Before
    public void setUp() {
        user = GetRandomUser.getRandomUser();
        Response resp = user.createUser(new UserModel(user.getEmail(), user.getPassword(), user.getName()));

        //Костыль, чтобы тесты работали и созданный пользователь удалялся в Before. Должен быть статус 201
        if (resp.statusCode() == SC_OK) {
            token = resp.path("accessToken");
        }
    }

    @After
    public void deleteUser() {
        if (token != null) {
            user.deleteUser(token);
        }
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    @Description("Логин под существующим пользователем. Возвращает 200 ОК")
    public void loginWithExistUser() {
        user.loginUser(new UserModel(user.getEmail(), user.getPassword(), user.getName()))
                .then().statusCode(SC_OK);
    }

    @Test
    @DisplayName("Логин под несуществующим пользователем")
    @Description("Логин под несуществующим пользователем. Возвращает 401 Unauthorized")
    public void loginWithNonExistUser() {
        String expectedMessage = "email or password are incorrect";
        Response resp = user.loginUser(new UserModel("TestEmailShouldReturnEror", "23gbj7423frtbtt", "RandomUser0054378"));

        assertEquals(SC_UNAUTHORIZED, resp.statusCode());
        assertEquals(expectedMessage, resp.path("message"));
    }

    @Test
    @DisplayName("Логин без почтового адреса")
    @Description("Логин без почтового адреса. Возвращает 401 Unauthorized")
    public void loginWithoutEmail() {
        String expectedMessage = "email or password are incorrect";
        Response resp = user.loginUser(new UserModel(null, "23gbj7423frtbtt", "RandomUser0054378"));

        assertEquals(SC_UNAUTHORIZED, resp.statusCode());
        assertEquals(expectedMessage, resp.path("message"));
    }

    @Test
    @DisplayName("Логин без пароля")
    @Description("Логин без пароля. Возвращает 401 Unauthorized")
    public void loginWithoutPassword() {
        String expectedMessage = "email or password are incorrect";
        Response resp = user.loginUser(new UserModel(user.getEmail(), null, "RandomUser0054378"));

        assertEquals(SC_UNAUTHORIZED, resp.statusCode());
        assertEquals(expectedMessage, resp.path("message"));
    }

    @Test
    @DisplayName("Логин без имени")
    @Description("Логин без имени. Возвращает 401 Unauthorized")
    public void loginWithoutName() {
        String expectedMessage = "email or password are incorrect";
        Response resp = user.loginUser(new UserModel(user.getEmail(), user.getPassword(), null));

        assertEquals(SC_UNAUTHORIZED, resp.statusCode());
        assertEquals(expectedMessage, resp.path("message"));
    }

    @Test
    @DisplayName("Логин с неправильным паролем")
    @Description("Логин с неправильным паролем. Возвращает 401 Unauthorized")
    public void loginWithIncorrectPassword() {
        String expectedMessage = "email or password are incorrect";
        Response resp = user.loginUser(new UserModel(user.getEmail(), "RandomIncorrectPassword124rg", user.getName()));

        assertEquals(SC_UNAUTHORIZED, resp.statusCode());
        assertEquals(expectedMessage, resp.path("message"));
    }

    @Test
    @DisplayName("Логин с неправильной почтой")
    @Description("Логин с неправильной почтой. Возвращает 401 Unauthorized")
    public void loginWithIncorrectEmail() {
        String expectedMessage = "email or password are incorrect";
        Response resp = user.loginUser(new UserModel("eirjvreiu4234rvo@fdiv.ru", user.getPassword(), user.getName()));

        assertEquals(SC_UNAUTHORIZED, resp.statusCode());
        assertEquals(expectedMessage, resp.path("message"));

    }

}