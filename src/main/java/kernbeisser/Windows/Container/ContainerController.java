package kernbeisser.Windows.Container;

import java.util.ArrayList;
import java.util.Collection;
import kernbeisser.DBEntities.ArticleKornkraft;
import kernbeisser.DBEntities.Container;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.MVC.Controller;
import org.jetbrains.annotations.NotNull;

public class ContainerController extends Controller<ContainerView, ContainerModel> {

  public ContainerController(User user) {
    super(new ContainerModel(user));
  }

  private void refreshUnpaidContainers() {
    Collection<Container> newContainers = model.getNewContainers();
    Collection<Container> oldContainers = model.getOldContainers();
    Collection<Container> containers = new ArrayList<>(newContainers.size() + oldContainers.size());
    containers.addAll(oldContainers);
    containers.addAll(newContainers);
    getView().setUnpaidContainers(containers);
  }

  public void commit() {
    Container newContainer = new Container();
    newContainer.setAmount(getView().getAmount());
    ArticleKornkraft item = model.getItemByKbNumber(getView().getKbNumber());
    if (item == null) {
      item = model.getItemByKkNumber(getView().getKkNumber());
      if (item == null) {
        getView().noItemFound();
        return;
      }
    }
    newContainer.setItem(item);
    newContainer.setPayed(false);
    newContainer.setNetPrice(getView().getNetPrice());
    newContainer.setAmount(getView().getAmount());
    newContainer.setUser(model.getUser());
    newContainer.setNetPrice(getView().getNetPrice());
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
    model.removeNew(getView().getSelectedUnpaidOrder());
    refreshUnpaidContainers();
  }

  public void searchKK() {
    clear();
    getView().setKbNumber("");
    pasteData(model.getItemByKkNumber(getView().getKkNumber()));
  }

  public void searchKB() {
    clear();
    getView().setKkNumber("");
    pasteData(model.getItemByKbNumber(getView().getKbNumber()));
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
    getView().setNetPrice("");
    getView().setItemSize("1");
    getView().setAmount("");
    getView().setItemName("Kein Artikel ausgewählt");
    getView().setSellingPrice("");
    // getView().setSuppliersItemNumber("");
    // getView().setKbNumber("");
  }

  private void pasteData(Container c) {
    getView()
        .setItemSize(
            c.getItem().getContainerSize()
                + " x "
                + c.getItem().getAmount()
                + c.getItem().getMetricUnits().getShortName());
    getView().setKbNumber(String.valueOf(c.getKBNumber()));
    getView().setKkNumber(String.valueOf(c.getItem().getSuppliersItemNumber()));
    getView().setSellingPrice(0 + "€");
    getView().setItemName(c.getItem().getName());
    getView().setAmount(String.valueOf(c.getAmount()));
    getView().setNetPrice(c.getNetPrice() + "€");
  }

  @Override
  public @NotNull ContainerModel getModel() {
    return model;
  }

  @Override
  public void fillView(ContainerView containerView) {
    getView().setLastContainers(model.getLastContainers());
    getView().setInsertSectionEnabled(PermissionKey.ACTION_ORDER_CONTAINER.userHas());
    refreshUnpaidContainers();
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }
}
