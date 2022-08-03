import Model.OrderModel;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class Order extends Specification {

    public Order() {
    }

    public List<String> getRandomIngredients(List<String> ingredients) {
        if (ingredients != null) {
            return ingredients.subList(0, new Random().nextInt(ingredients.size()));
        } else {
            return null;
        }
    }

    @Step("Получение данных об ингредиентах")
    public Response getIngredients() {
        return given()
                .spec(setRequestSpecification())
                .get("/api/ingredients")
                .then()
                .extract()
                .response();
    }

    @Step("Создание заказа")
    public Response createOrder(OrderModel orderModel, String token) {
        if (token != null) {
            token = token.split(" ")[1];
            return given()
                    .spec(setRequestSpecification())
                    .auth().oauth2(token)
                    .body(orderModel)
                    .post("/api/orders")
                    .then()
                    .extract()
                    .response();
        } else {
            return given()
                    .spec(setRequestSpecification())
                    .body(orderModel)
                    .post("/api/orders")
                    .then()
                    .extract()
                    .response();
        }
    }

    @Step("Получение заказов определённого пользователя")
    public Response gerUserOrders(String token) {
        if (token != null) {
            token = token.split(" ")[1];
            return given()
                    .spec(setRequestSpecification())
                    .auth().oauth2(token)
                    .get("/api/orders")
                    .then()
                    .extract()
                    .response();
        } else {
            return given()
                    .spec(setRequestSpecification())
                    .get("/api/orders")
                    .then()
                    .extract()
                    .response();
        }
    }
}
