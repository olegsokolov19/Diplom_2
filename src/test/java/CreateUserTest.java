import Model.UserModel;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;

public class CreateUserTest {
    User user = GetRandomUser.getRandomUser();

    @After
    public void deleteUser() {
        Response resp = user.loginUser(new UserModel(user.getEmail(), user.getPassword(), user.getName()))
                .then()
                .extract()
                .response();

        String token = resp.path("accessToken");

        //Костыль, чтобы тесты работали и созданный пользователь удалялся. Должен быть статус 201
        if (resp.statusCode() == SC_OK) {
            user.deleteUser(token);
        }
    }

    @Test
    @DisplayName("Создание уникального курьера")
    @Description("Создаём курьера с уникальными данными, которых нет в базе. Возврващает 201 Created")
    public void createUniqueUser() {
        user.createUser(new UserModel(user.getEmail(), user.getPassword(), user.getName()))
                .then()
                .statusCode(SC_CREATED);
    }

    @Test
    @DisplayName("Создание неуникального курьера")
    @Description("Создаём курьера с данными, которые уже есть в базе. Возвращает 403 Forbidden. " +
            "Тест заблокирован дефектом при создании пользователя. Создание должно возвращать 201, а возвращает 200")
    public void createNonUniqueUser() {
        String email = user.getEmail();
        String password = user.getPassword();
        String name = user.getName();

        user.createUser(new UserModel(email, password, name))
                .then()
                .statusCode(SC_CREATED);

        user.createUser(new UserModel(email, password, name))
                .then()
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    @DisplayName("Создание уникального курьера с незаполненной почтой")
    @Description("Создаём курьера с незаполненной почтой. Возвращает 403 Forbidden")
    public void createUserWithoutEmail() {
        user.createUser(new UserModel(null, user.getPassword(), user.getName()))
                .then()
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    @DisplayName("Создание уникального курьера с незаполненным паролем")
    @Description("Создаём курьера с незаполненным паролем. Возвращает 403 Forbidden")
    public void createUserWithoutPassword() {
        user.createUser(new UserModel(user.getEmail(), null, user.getName()))
                .then()
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    @DisplayName("Создание уникального курьера с незаполненным именем")
    @Description("Создаём курьера с незаполненным именем. Возвращает 403 Forbidden")
    public void createUserWithoutName() {
        user.createUser(new UserModel(user.getEmail(), user.getPassword(), null))
                .then()
                .statusCode(SC_FORBIDDEN);
    }

}