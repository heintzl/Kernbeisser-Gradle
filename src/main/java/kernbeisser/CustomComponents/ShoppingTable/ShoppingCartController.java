package kernbeisser.CustomComponents.ShoppingTable;

import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.Key;
import kernbeisser.Price.PriceCalculator;
import kernbeisser.Windows.Controller;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ShoppingCartController implements Controller<ShoppingCartView,ShoppingCartModel> {
    private ShoppingCartView view;
    private ShoppingCartModel model;


    public ShoppingCartController(double userValue, int userSurcharge) {
        model = new ShoppingCartModel(userValue, userSurcharge);
        view = new ShoppingCartView(this);
    }

    public void addShoppingItem(ShoppingItem item, boolean stack) {
        model.addItem(item, stack);
        refresh();
    }

    double getPrice(ShoppingItem item) {
        return PriceCalculator.getShoppingItemPrice(item, model.getUserSurcharge());
    }

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

    public Collection<ShoppingItem> getItems(){
        return model.getItems();
    }

    @Override
    public @NotNull ShoppingCartView getView() {
        return view;
    }

    @Override
    public @NotNull ShoppingCartModel getModel() {
        return model;
    }
  
    @Override
    public void fillUI() {
        refresh();
    }

    @Override
    public Key[] getRequiredKeys() {
        return new Key[0];
    }

}
