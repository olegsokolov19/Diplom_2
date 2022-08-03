import Model.OrderModel;
import Model.UserModel;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class CreateOrderTest {

    Order order;
    User user;
    String token;

    private List<String> ingredients;

    @Before
    public void setUp() {
        order = new Order();
        ingredients = order.getIngredients().path("data._id");
        user = GetRandomUser.getRandomUser();
        token = user.createUser(new UserModel(user.getEmail(), user.getPassword(), user.getName())).path("accessToken");
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и с ингредиентами")
    @Description("Создание заказа с валидными данными, с авторизацией и с ингредиентами. Возвращает 200 ОК и номер заказа")
    public void createOrderWithAuthorization() {
        Response resp = order.createOrder(new OrderModel(order.getRandomIngredients(ingredients)), token);

        assertEquals(SC_OK, resp.statusCode());
        assertEquals(true, resp.path("success"));
        assertNotNull(resp.path("order.number"));
    }

    @Test
    @DisplayName("Создание заказа без авторизациии")
    @Description("Создание заказа с валидными данными и без авторизации. Возвращает 401 UNAUTHORIZED")
    public void createOrderWithoutAuthorization() {
        Response resp = order.createOrder(new OrderModel(order.getRandomIngredients(ingredients)), null);

        assertEquals(SC_UNAUTHORIZED, resp.statusCode());
        assertEquals(false, resp.path("success"));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и без ингредиентов")
    @Description("Создание заказа с авторизацией и без ингредиентов. Возвращает 400 Bad Request")
    public void createOrderWithoutIngredients() {
        Response resp = order.createOrder(new OrderModel(order.getRandomIngredients(null)), token);

        assertEquals(SC_BAD_REQUEST, resp.statusCode());
        assertEquals(false, resp.path("success"));
    }

    @Test
    @DisplayName("Создание заказа с невалидным хэшем ингредиента")
    @Description("Создание заказа с невалидным хэшем ингредиента. Возвращает 500 Internal Server Error")
    public void createOrderWithIncorrectIngredients() {
        Response resp = order.createOrder(new OrderModel(order.getRandomIngredients(List.of("61c23vf001bdaaa70"))), token);

        assertEquals(SC_INTERNAL_SERVER_ERROR, resp.statusCode());
        assertEquals(false, resp.path("success"));
    }

}
