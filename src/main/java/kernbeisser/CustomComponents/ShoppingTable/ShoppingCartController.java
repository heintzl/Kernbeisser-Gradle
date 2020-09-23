package kernbeisser.CustomComponents.ShoppingTable;

import java.util.List;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.MVC.IController;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class ShoppingCartController implements IController<ShoppingCartView, ShoppingCartModel> {
  private final ShoppingCartModel model;
  private ShoppingCartView view;
  @Linked private final boolean editable;

  /**
   * @param userValue The users credit before purchase
   * @param userSurcharge The solidarity surcharge to apply to ShoppingItems
   * @param editable true: cart can be used for shopping: items can be added and deleted, false:
   *     cart is for display only - use view.setObjects to render cart
   */
  public ShoppingCartController(double userValue, double userSurcharge, boolean editable) {
    model = new ShoppingCartModel(userValue, userSurcharge);
    this.editable = editable;
  }

  public void addShoppingItem(ShoppingItem item) {
    // TODO should throw exception if !editable
    if (!editable) return;
    ShoppingItem addedItem = model.addItem(item);
    if (item.getSingleDeposit() != 0) {
      model.addItem(addedItem.createSingleDeposit(item.getItemMultiplier()));
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
              if (Math.signum(containers) == Math.signum(item.getItemMultiplier())) {
                model.addItemBehind(addedItem.createContainerDeposit(containers), addedItem);
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
    double sum = 0;
    view.setObjects(model.getItems());
    for (ShoppingItem item : model.getItems()) {
      sum += item.getRetailPrice();
    }
    view.setSum(sum);
    view.setValue(model.getUserValue() - sum);
  }

  void delete(ShoppingItem i) {
    // TODO should throw exception if !editable
    if (!editable) return;
    model.getItems().removeIf(e -> e.getParentItem() == i || e.equals(i));
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

  public void manipulateShoppingItemAmount(ShoppingItem i, int number) {
    int manipulate = i.isContainerDiscount() ? (int) (number * i.getContainerSize()) : number;
    if (i.getItemMultiplier() + manipulate <= 0) {
      delete(i);
      return;
    }
    model.getItems().stream()
        .filter(e -> (e.getParentItem() == i) || e.equals(i))
        .forEach(
            e -> {
              if (e.getParentItem() == i && e.getName().contains("Gebinde")) {
                if (i.isContainerDiscount()) {
                  e.setItemMultiplier(e.getItemMultiplier() + number);
                }
              } else {
                e.setItemMultiplier(e.getItemMultiplier() + manipulate);
              }
            });
    refresh();
  }

  void plus(ShoppingItem i) {
    manipulateShoppingItemAmount(i, +1);
  }

  void minus(ShoppingItem i) {
    manipulateShoppingItemAmount(i, -1);
  }
}
