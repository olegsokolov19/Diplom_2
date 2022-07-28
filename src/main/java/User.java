import Model.UserModel;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class User extends Specification {
    private String email;
    private String password;
    private String name;

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Step("Создание пользователя")
    public Response createUser(UserModel userModel) {
        return given()
                .spec(setRequestSpecification())
                .body(userModel)
                .when()
                .post("/api/auth/register")
                .then()
                .extract()
                .response();
    }

    @Step("Авторизация пользователя")
    public Response loginUser(UserModel userModel) {
        return given()
                .spec(setRequestSpecification())
                .body(userModel)
                .when()
                .post("/api/auth/login")
                .then()
                .extract()
                .response();
    }


    @Step("Удаление пользователя")
    public Response deleteUser(String token) {
        token = token.split(" ")[1];
        return given()
                .spec(setRequestSpecification())
                .auth().oauth2(token)
                .delete("/api/auth/user")
                .then()
                .extract()
                .response();
    }

    @Step("Изменение данных пользователя")
    public Response updateUser(UserModel userModel, String token) {
        if (token != null) {
            token = token.split(" ")[1];
            return given()
                    .spec(setRequestSpecification())
                    .auth().oauth2(token)
                    .body(userModel)
                    .when()
                    .patch("/api/auth/user")
                    .then()
                    .extract()
                    .response();
        } else {
            return given()
                    .spec(setRequestSpecification())
                    .body(userModel)
                    .when()
                    .patch("/api/auth/user")
                    .then()
                    .extract()
                    .response();
        }
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
