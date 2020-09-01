package kernbeisser.Windows.Container;

import java.util.ArrayList;
import java.util.Collection;
import kernbeisser.DBEntities.ArticleKornkraft;
import kernbeisser.DBEntities.Container;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.MVC.IController;
import org.jetbrains.annotations.NotNull;

public class ContainerController implements IController<ContainerView, ContainerModel> {
  private ContainerView view;
  private final ContainerModel model;

  public ContainerController(User user) {
    model = new ContainerModel(user);
  }

  private void refreshUnpaidContainers() {
    Collection<Container> newContainers = model.getNewContainers();
    Collection<Container> oldContainers = model.getOldContainers();
    Collection<Container> containers = new ArrayList<>(newContainers.size() + oldContainers.size());
    containers.addAll(oldContainers);
    containers.addAll(newContainers);
    view.setUnpaidContainers(containers);
  }

  public void commit() {
    Container newContainer = new Container();
    newContainer.setAmount(view.getAmount());
    ArticleKornkraft item = model.getItemByKbNumber(view.getKbNumber());
    if (item == null) {
      item = model.getItemByKkNumber(view.getKkNumber());
      if (item == null) {
        view.noItemFound();
        return;
      }
    }
    newContainer.setItem(item);
    newContainer.setPayed(false);
    newContainer.setNetPrice(view.getNetPrice());
    newContainer.setAmount(view.getAmount());
    newContainer.setUser(model.getUser());
    newContainer.setNetPrice(view.getNetPrice());
    model.addContainer(newContainer);
    refreshUnpaidContainers();
    clear();
  }

  @Override
  public boolean commitClose() {
    model.saveChanges();
    return true;
  }

  public void remove() {
    model.removeNew(view.getSelectedUnpaidOrder());
    refreshUnpaidContainers();
  }

  public void searchKK() {
    clear();
    view.setKbNumber("");
    pasteData(model.getItemByKkNumber(view.getKkNumber()));
  }

  public void searchKB() {
    clear();
    view.setKkNumber("");
    pasteData(model.getItemByKbNumber(view.getKbNumber()));
  }

  private void pasteData(ArticleKornkraft item) {
    if (item != null) {
      Container c = new Container();
      c.setItem(item);
      c.setAmount(1);
      c.setNetPrice(0);
      c.setPayed(false);
      pasteData(c);
    }
  }

  private void clear() {
    view.setNetPrice("");
    view.setItemSize("1");
    view.setAmount("");
    view.setItemName("Kein Artikel ausgewählt");
    view.setSellingPrice("");
    // view.setSuppliersItemNumber("");
    // view.setKbNumber("");
  }

  private void pasteData(Container c) {
    view.setItemSize(
        c.getItem().getContainerSize()
            + " x "
            + c.getItem().getAmount()
            + c.getItem().getMetricUnits().getShortName());
    view.setKbNumber(String.valueOf(c.getKBNumber()));
    view.setKkNumber(String.valueOf(c.getItem().getSuppliersItemNumber()));
    view.setSellingPrice(0 + "€");
    view.setItemName(c.getItem().getName());
    view.setAmount(String.valueOf(c.getAmount()));
    view.setNetPrice(c.getNetPrice() + "€");
  }

  @Override
  public @NotNull ContainerModel getModel() {
    return model;
  }

  @Override
  public void fillUI() {
    view.setLastContainers(model.getLastContainers());
    view.setInsertSectionEnabled(PermissionKey.ACTION_ORDER_CONTAINER.userHas());
    refreshUnpaidContainers();
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }
}
