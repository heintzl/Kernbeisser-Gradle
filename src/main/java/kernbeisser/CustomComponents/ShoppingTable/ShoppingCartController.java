package kernbeisser.CustomComponents.ShoppingTable;

import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Price.PriceCalculator;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;

import java.util.Collection;

public class ShoppingCartController implements Controller {
    private ShoppingCartView view;
    private ShoppingCartModel model;


    public ShoppingCartController(double userValue, int userSurcharge) {
        model = new ShoppingCartModel(userValue, userSurcharge);
        view = new ShoppingCartView(this);
        refresh();
    }

    public void addShoppingItem(ShoppingItem item, boolean stack) {
        model.addItem(item, stack);
        refresh();
    }

    double getPrice(ShoppingItem item) {
        return PriceCalculator.getShoppingItemPrice(item, model.getUserSurcharge());
    }

    @Override
    public void refresh() {
        view.clearNodes();
        double sum = 0;
        view.setObjects(model.getItems());
        for (ShoppingItem item : model.getItems()) {
            sum += PriceCalculator.getShoppingItemPrice(item, model.getUserSurcharge());
        }
        view.setSum(sum);
        view.setValue(model.getUserValue() - sum);
        view.repaint();
    }

    void delete(ShoppingItem i) {
        model.getItems().remove(i);
        refresh();
    }

    @Override
    public ShoppingCartView getView() {
        return view;
    }

    @Override
    public Model getModel() {
        return model;
    }

    public Collection<ShoppingItem> getItems() {
        return model.getItems();
    }

}
