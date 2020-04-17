package kernbeisser.Windows.Container;

import kernbeisser.DBEntities.ArticleKornkraft;
import kernbeisser.DBEntities.Container;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Key;
import kernbeisser.Windows.Controller;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class ContainerController implements Controller<ContainerView,ContainerModel> {
    private final ContainerView view;
    private final ContainerModel model;

    public ContainerController(User user) {
        model = new ContainerModel(user);
        view = new ContainerView(this);

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
        Container toSave = new Container();
        Container currentContainer = model.getCurrentContainer();
        toSave.setAmount(view.getAmount());
        toSave.setUser(model.getUser());
        toSave.setNetPrice(currentContainer.getNetPrice());
        toSave.setItem(currentContainer.getItem());
        toSave.setPayed(false);
        model.addContainer(toSave);
        refreshUnpaidContainers();
        //clear();
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
        editCurrentContainer(model.getItemByKkNumber(view.getKkNumber()));
    }

    public void searchKB() {
        clear();
        view.setKkNumber("");
        editCurrentContainer(model.getItemByKbNumber(view.getKbNumber()));
    }

    private void editCurrentContainer(ArticleKornkraft item) {
        if (item != null) {
            Container c = model.getCurrentContainer();
            c.setItem(item);
            if (view.getAmount() == null || view.getAmount() == 0) {
                c.setAmount(1);
            } else {
                c.setAmount(view.getAmount());
            }
            c.setNetPrice(item.getNetPrice());
            insertDataInView(c);
        }
    }

    private void clear() {
        view.setNetPrice("");
        view.setItemSize("1");
        //view.setAmount("");
        view.setItemName("Kein Artikel ausgewählt");
        view.setSellingPrice("");
        //view.setKkNumber("");
        //view.setKbNumber("");
    }

    private void insertDataInView(Container c) {
        view.setItemSize(c.getItem().getContainerSize() + " x " + c.getItem().getAmount() + c.getItem()
                                                                                             .getMetricUnits().getShortName());
        view.setKbNumber(String.valueOf(c.getKBNumber()));
        view.setKkNumber(String.valueOf(c.getItem().getKkNumber()));
        view.setSellingPrice(0  + "€");             //TODO: Calculation of Selling Price
        view.setItemName(c.getItem().getName());
        view.setAmount(String.valueOf(c.getAmount()));
        view.setNetPrice(String.valueOf(c.getNetPrice())); // TODO: Find out, in which unit NetPrice is given, the table must also be adapted
    }

    @Override
    public @NotNull ContainerView getView() {
        return view;
    }

    @Override
    public @NotNull ContainerModel getModel() {
        return model;
    }

    @Override
    public void fillUI() {
        view.setLastContainers(model.getLastContainers());
        view.setInsertSectionEnabled(Key.ACTION_ORDER_CONTAINER.userHas());
        refreshUnpaidContainers();
    }

    @Override
    public Key[] getRequiredKeys() {
        return new Key[0];
    }

    public void copy(Container container) {
        model.setCurrentContainer(container);
        insertDataInView(model.getCurrentContainer());
    }
}
