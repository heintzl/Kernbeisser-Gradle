package kernbeisser.CustomComponents.ShoppingTable;

import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Price.PriceCalculator;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.View;

public class ShoppingCartController implements Controller {
    private ShoppingCartView view;
    private ShoppingCartModel model;


    public ShoppingCartController(int userValue,int userSurcharge){
        model = new ShoppingCartModel(userValue,userSurcharge);
        view = new ShoppingCartView(this);
        refresh();
    }

    public void addShoppingItem(ShoppingItem item){
        model.addItem(item);
        refresh();
    }

    int getPrice(ShoppingItem item){
        return PriceCalculator.getShoppingItemPrice(item,model.getUserSurcharge());
    }

    @Override
    public void refresh() {
        view.clearNodes();
        int sum = 0;
        view.setObjects(model.getItems());
        for (ShoppingItem item : model.getItems()) {
            sum+= PriceCalculator.getShoppingItemPrice(item,model.getUserSurcharge());
        }
        view.setSum(sum);
        view.setValue(model.getUserValue()-sum);
        view.repaint();
    }

    void delete(ShoppingItem i){
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

}
