package kernbeisser.CustomComponents.ShoppingTable;

import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.Controller;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.text.MessageFormat;
import java.util.List;

public class ShoppingCartController implements Controller<ShoppingCartView,ShoppingCartModel> {
    private ShoppingCartView view;
    private ShoppingCartModel model;


    public ShoppingCartController(double userValue, double userSurcharge) {
        model = new ShoppingCartModel(userValue, userSurcharge);
        view = new ShoppingCartView(this);
    }

    public void addShoppingItem(ShoppingItem item, boolean piece) {
        int itemIndex = model.addItem(item, piece);
        if (item.getShoppingCartIndex() == 0){
            item.setShoppingCartIndex(itemIndex);
        }
        if (item.getSingleDeposit() != 0) {

            model.addItem(item.createItemDeposit(), true);
        }
        if (item.getContainerDeposit() != 0 && item.getContainerSize() > 0) {
            if (item.getItemMultiplier() >= item.getContainerSize()) {
                int containers = 0;
                boolean exit = false;
                String initValue = MessageFormat.format("{0, number, 0}",
                                                        Math.floor(item.getItemMultiplier() / item.getContainerSize()))
                                                .trim();

                String response = JOptionPane.showInputDialog(
                        view,
                        MessageFormat.format(
                                "Die eingegebene Menge passt in ein oder mehrere {0, number, 0}er Pfand-Gebinde. Für wie viele Gebinde soll Pfand berechnet werden?",
                                item.getContainerSize()),
                        initValue
                );
                if (response != null) {
                    response = response.trim();
                }
                do {
                    if (response == null || response.hashCode() == 0 || response.hashCode() == 48) {
                        exit = true;
                    } else {
                        try {
                            containers = Integer.parseInt(response);
                            if (containers > 0) {
                                model.addItemBehind(item.createContainerDeposit(containers), item,true);
                                exit = true;
                            } else {
                                throw (new NumberFormatException());
                            }
                        } catch (NumberFormatException exception) {
                            response = JOptionPane.showInputDialog(
                                    view,
                                    "Eingabe kann nicht verarbeitet werden, bitte noch einmal versuchen. Für wie viele Gebinde soll Pfand berechnet werden?",
                                    initValue
                            );
                        }
                    }
                } while (!exit);
            }
        }
        refresh();
    }

    double getPrice(ShoppingItem item) {
        return item.getRetailPrice();
    }

    public void refresh() {
        view.clearNodes();
        double sum = 0;
        view.setObjects(model.getItems());
        for (ShoppingItem item : model.getItems()) {
            sum += item.getRetailPrice();
        }
        view.setSum(sum);
        view.setValue(model.getUserValue() - sum);
        view.repaint();
    }

    void delete(ShoppingItem i) {
        model.getItems().remove(i);
        refresh();
    }

    public List<ShoppingItem> getItems() {
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
    public PermissionKey[] getRequiredKeys() {
        return new PermissionKey[0];
    }

}
