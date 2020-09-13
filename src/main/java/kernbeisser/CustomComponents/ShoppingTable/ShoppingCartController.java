package kernbeisser.CustomComponents.ShoppingTable;

import java.util.List;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.MVC.Controller;
import org.jetbrains.annotations.NotNull;

public class ShoppingCartController implements Controller<ShoppingCartView, ShoppingCartModel> {
  private final ShoppingCartView view;
  private final ShoppingCartModel model;
  private final boolean editable;

  /**
   * @param userValue The users credit before purchase
   * @param userSurcharge The solidarity surcharge to apply to ShoppingItems
   * @param editable true: cart can be used for shopping: items can be added and deleted, false:
   *     cart is for display only - use view.setObjects to render cart
   */
  public ShoppingCartController(double userValue, double userSurcharge, boolean editable) {
    model = new ShoppingCartModel(userValue, userSurcharge);
    view = new ShoppingCartView(this, editable);
    this.editable = editable;
  }

  public void addShoppingItem(ShoppingItem item) {
    // TODO should throw exception if !editable
    if (!editable) return;
    int itemIndex = model.addItem(item);
    if (item.getShoppingCartIndex() == 0) {
      item.setShoppingCartIndex(itemIndex);
    }
    if (item.getSingleDeposit() != 0) {
      model.addItem(item.createSingleDeposit());
    }
    if (item.getContainerDeposit() != 0 && item.getContainerSize() > 0) {
      if (Math.abs(item.getItemMultiplier()) >= item.getContainerSize()) {
        int containers = 0;
        boolean exit = false;
        String response = view.inputNoOfContainers(item, false);
        do {
          if (response == null || response.equals("")) {
            exit = true;
          } else {
            try {
              containers = Integer.parseInt(response);
              if (containers == 0) {
                exit = true;
              } else if (Math.signum(containers) == Math.signum(item.getItemMultiplier())) {
                model.addItemBehind(item.createContainerDeposit(containers), item);
                exit = true;
              } else {
                throw (new NumberFormatException());
              }
            } catch (NumberFormatException exception) {
              response = view.inputNoOfContainers(item, true);
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
    // TODO should throw exception if !editable
    if (!editable) return;
    model.getItems().remove(i);
    refresh();
  }

  public void emptyCart() {
    // TODO should throw exception if !editable
    if (!editable) return;
    model.getItems().clear();
    refresh();
  }

  public List<ShoppingItem> getItems() {
    return model.getItems();
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
