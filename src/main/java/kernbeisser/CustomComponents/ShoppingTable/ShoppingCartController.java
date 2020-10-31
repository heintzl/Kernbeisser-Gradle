package kernbeisser.CustomComponents.ShoppingTable;

import java.util.List;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Dialogs.RememberDialog;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class ShoppingCartController extends Controller<ShoppingCartView, ShoppingCartModel> {
  @Linked private final boolean editable;

  /**
   * @param userValue The users credit before purchase
   * @param userSurcharge The solidarity surcharge to apply to ShoppingItems
   * @param editable true: cart can be used for shopping: items can be added and deleted, false:
   *     cart is for display only - use getView().setObjects to render cart
   */
  public ShoppingCartController(double userValue, double userSurcharge, boolean editable) {
    super(new ShoppingCartModel(userValue, userSurcharge));
    this.editable = editable;
  }

  public void addShoppingItem(ShoppingItem item) {
    // TODO should throw exception if !editable
    if (!editable) return;
    ShoppingItem addedItem = model.addItem(item);
    if (item.getItemMultiplier() != addedItem.getItemMultiplier())
      RememberDialog.showDialog(
          LogInModel.getLoggedIn(),
          getView().getContent(),
          "Der Artikel hat bereits im Einkaufswagen exsistiert.\nDie Menge von "
              + addedItem.getName()
              + " wurde auf "
              + addedItem.getItemMultiplier()
              + " geändert.",
          "Artikel existiert bereits im Einkaufswagen");
    if (item.getSingleDeposit() != 0) {
      model.addItem(addedItem.createSingleDeposit(item.getItemMultiplier()));
    }
    if (item.getContainerDeposit() != 0 && item.getContainerSize() > 0) {
      if (Math.abs(item.getItemMultiplier()) >= item.getContainerSize()) {
        int containers = 0;
        boolean exit = false;
        String response = getView().inputNoOfContainers(item, false);
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
              response = getView().inputNoOfContainers(item, true);
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
    getView().setObjects(model.getItems());
    for (ShoppingItem item : model.getItems()) {
      sum += item.getRetailPrice();
    }
    getView().setSum(sum);
    getView().setValue(model.getUserValue() - sum);
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

  public void manipulateShoppingItemAmount(ShoppingItem i, int number) {
    final int itemMultiplier = i.getItemMultiplier();
    final double containerSize = i.getContainerSize();
    final int itemNumber = i.isContainerDiscount() ? (int) (number * i.getContainerSize()) : number;
    model
        .getItems()
        .removeIf(
            e -> {
              if (e.equals(i)) {
                e.setItemMultiplier(itemMultiplier + itemNumber);
                return e.getItemMultiplier() <= 0;
              }
              if (e.getParentItem() == null || (!e.getParentItem().equals(i))) return false;
              if (e.getName().contains("Gebinde")) {
                if (e.getParentItem().getItemMultiplier() <= 0) return true;
                int before = (int) (itemMultiplier / containerSize);
                int after = (int) ((itemMultiplier + itemNumber) / containerSize);
                e.setItemMultiplier(e.getItemMultiplier() + after - before);
              } else {
                e.setItemMultiplier(e.getItemMultiplier() + itemNumber);
              }
              return e.getItemMultiplier() <= 0;
            });
    if (number > 0 && i.getSingleDeposit() != 0) {
      boolean depositFound = false;
      for (ShoppingItem item : model.getItems()) {
        if (item.getParentItem() != null
            && item.getParentItem().equals(i)
            && !item.getName().contains("Gebinde")) {
          depositFound = true;
          break;
        }
      }
      if (!depositFound) {
        model.addItemBehind(i.createItemDeposit(itemNumber, false), i);
      }
    }
    if ((itemMultiplier + number) / containerSize == 1.
        && (!model.getItems().stream()
            .anyMatch(
                e ->
                    e.getParentItem() != null
                        && e.getParentItem().equals(i)
                        && e.getName().contains("Gebinde")))) {
      model.addItemBehind(i.createItemDeposit(1, true), i);
    }
    refresh();
  }

  public void setValueAfterLabel(String text) {
    getView().setValueAfterLabel(text);
  }

  void plus(ShoppingItem i) {
    manipulateShoppingItemAmount(i, +1);
  }

  void minus(ShoppingItem i) {
    manipulateShoppingItemAmount(i, -1);
  }

  @Override
  public void fillView(ShoppingCartView shoppingCartView) {
    refresh();
  }
}
