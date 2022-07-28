import Model.OrderModel;
import Model.UserModel;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GetUserOrdersTest {
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
    @DisplayName("Получение заказов пользователя, у которого нет заказов")
    @Description("Получение заказов пользователя без заказов. Возвращает 200 ОК, пустой массив заказов")
    public void getUserOrdersWithoutOrders() {
        ArrayList<String> expectedOrders = new ArrayList<>();
        Integer expectedTotal = 0;
        Integer expectedTotalToday = 0;
        Response resp = order.gerUserOrders(token);

        assertEquals(true, resp.path("success"));
        assertEquals(expectedOrders, resp.path("orders"));
        assertEquals(expectedTotal, resp.path("total"));
        assertEquals(expectedTotalToday, resp.path("totalToday"));

    }

    @Test
    @DisplayName("Получение заказов пользователя, у которого есть 5 заказов")
    @Description("Получение заказов пользователя с 5-ю заказами. Возвращает 200 ОК и массив с 5-ю заказами")
    public void getUserOrdersWithFiveOrders() {
        ArrayList<Integer> expectedOrders = new ArrayList<>();

        while (expectedOrders.size() <= 5) {
            Response resp = order.createOrder(new OrderModel(order.getRandomIngredients(ingredients)), token);
            Integer orderNumber = resp.path("order.number");
            if (orderNumber != null) {
                expectedOrders.add(orderNumber);
            }
        }

        Integer expectedTotal = 5;
        Integer expectedTotalToday = 5;
        Response resp = order.gerUserOrders(token);

        assertEquals(true, resp.path("success"));
        assertEquals(expectedOrders, resp.path("orders.number"));
        assertEquals(expectedTotal, resp.path("total"));
        assertEquals(expectedTotalToday, resp.path("totalToday"));
    }

}
