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

  private int extractContainerDeposit(ShoppingItem item) {
    return model.getItems().stream()
        .filter(i -> i.getName().contains("Gebinde") && i.getParentItem().equals(item))
        .map(ShoppingItem::getItemMultiplier)
        .findFirst()
        .orElse(0);
  }

  public void mergeShoppingItem(ShoppingItem item, boolean fromShoppingMask) {
    // TODO should throw exception if !editable
    if (!editable) return;
    ShoppingItem addedItem = model.addItem(item);
    if (addedItem == null) return;
    if (item.getItemMultiplier() != addedItem.getItemMultiplier()) {
      if (fromShoppingMask) {
        RememberDialog.showDialog(
            LogInModel.getLoggedIn(),
            "ArticleExistsInCart",
            getView().getContent(),
            "Der Artikel ist bereits im Einkaufswagen vorhanden.\nDie Menge von "
                + addedItem.getName()
                + " wird auf "
                + (addedItem.getDisplayAmount().equals("")
                    ? String.format("%.2f €", addedItem.getRetailPrice())
                    : addedItem.getDisplayAmount())
                + " geändert.",
            "Artikel existiert bereits im Einkaufswagen");
      } else {
        if (addedItem.getItemMultiplier() <= 0) {
          model.getItems().remove(addedItem);
        }
      }
    }
    if (item.getSingleDeposit() != 0) {
      model.addItem(addedItem.createSingleDeposit(item.getItemMultiplier()));
    }
    double containerSize = item.getContainerSize();
    if (containerSize > 0 && item.getContainerDeposit() != 0) {
      int containers =
          ((int) ((addedItem.getItemMultiplier()) / containerSize)
              - Math.round(extractContainerDeposit(addedItem)));
      if (containers != 0) {
        boolean exit = false;
        String response = getView().inputNoOfContainers(containers, containerSize, false);
        do {
          if (response == null || response.equals("")) {
            exit = true;
          } else {
            try {
              containers = Integer.parseInt(response);
              if (Math.signum(containers) == Math.signum(item.getItemMultiplier())) {
                model.addItemBehind(addedItem.createContainerDeposit(containers), addedItem);
                exit = true;
              } else if (containers == 0) {
                exit = true;
              } else {
                throw (new NumberFormatException());
              }
            } catch (NumberFormatException exception) {
              response = getView().inputNoOfContainers(containers, containerSize, true);
            }
          }
        } while (!exit);
      }
    }
    refresh();
  }

  public void addShoppingItem(ShoppingItem item) {
    mergeShoppingItem(item, true);
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
    model.delete(i);
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
    ShoppingItem newItem =
        new ShoppingItem(i.extractArticle(), i.getDiscount(), i.isContainerDiscount());
    newItem.setItemMultiplier(
        i.isContainerDiscount() ? (int) (number * i.getContainerSize()) : number);
    mergeShoppingItem(newItem, false);
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
