import Model.UserModel;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class UpdateUserTest {

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
    @DisplayName("Изменение почты пользователя с авторизацией")
    @Description("Изменяем почту польователя с авторизацией. Возвращает 200 ОК и новые данные в ответе")
    public void updateEmailAuthorizationUser() {
        String newEmail = RandomStringUtils.randomAlphabetic(8) + "yandex.com";
        Response resp = user.updateUser(new UserModel(newEmail, user.getPassword(), user.getName()), token);

        assertEquals(SC_OK, resp.statusCode());
        assertEquals(true, resp.path("success"));
        assertEquals(newEmail.toLowerCase(), resp.path("user.email"));
        assertEquals(user.getName(), resp.path("user.name"));
    }

    @Test
    @DisplayName("Изменение имя пользователя с авторизацией")
    @Description("Изменяем имя польователя с авторизацией. Возвращает 200 ОК и новые данные в ответе")
    public void updatePasswordAuthorizationUser() {
        String newName = RandomStringUtils.randomAlphabetic(6);
        Response resp = user.updateUser(new UserModel(user.getEmail(), user.getPassword(), newName), token);

        assertEquals(SC_OK, resp.statusCode());
        assertEquals(true, resp.path("success"));
        assertEquals(user.getEmail().toLowerCase(), resp.path("user.email"));
        assertEquals(newName, resp.path("user.name"));
    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    @Description("Изменяем данные польователя без авторизации. Возвращает 401 Unauthorized и ошибку")
    public void updateUserWithoutAuthorization() {
        String expectedMessage = "You should be authorised";

        String newEmail = RandomStringUtils.randomAlphabetic(8) + "yandex.com";
        Response resp = user.updateUser(new UserModel(newEmail, user.getPassword(), user.getName()), null);

        assertEquals(SC_UNAUTHORIZED, resp.statusCode());
        assertEquals(false, resp.path("success"));
        assertEquals(expectedMessage, resp.path("message"));
    }

    @Test
    @DisplayName("Изменение почты пользователя с авторизацией на почту, которая уже существует")
    @Description("Изменяем почту польователя с авторизацией на существующую почту. Возвращает 403 Forbidden и ошибку")
    public void updateExistEmailAuthorizationUser() {
        String expectedMessage = "User with such email already exists";
        UserModel userModel = new UserModel("existEmail@gmail.ru", user.getPassword(), user.getName());

        user.createUser(userModel);

        Response resp = user.updateUser(new UserModel(userModel.getEmail(), user.getPassword(), user.getName()), token);

        assertEquals(SC_FORBIDDEN, resp.statusCode());
        assertEquals(false, resp.path("success"));
        assertEquals(expectedMessage, resp.path("message"));
    }

}
