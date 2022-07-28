package Model;

import java.util.List;
import java.util.Random;

public class OrderModel {
    private List<String> ingredients;

    public OrderModel(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}
