package kernbeisser.Windows.Container;

import kernbeisser.DBEntities.Container;
import kernbeisser.DBEntities.ItemKK;
import kernbeisser.DBEntities.User;
import kernbeisser.Windows.Window;

import java.util.ArrayList;
import java.util.Collection;

public class ContainerController {
    private ContainerView view;
    private ContainerModel model;
    ContainerController(Window current, User user){
        model = new ContainerModel(user);
        view = new ContainerView(current,this);
        view.setLastContainers(model.getLastContainers());
        refreshUnpaidContainers();
    }

    private void refreshUnpaidContainers(){
        Collection<Container> newContainers = model.getNewContainers();
        Collection<Container> oldContainers = model.getOldContainers();
        Collection<Container> containers = new ArrayList<>(newContainers.size()+oldContainers.size());
        containers.addAll(oldContainers);
        containers.addAll(newContainers);
        view.setUnpaidContainers(containers);
    }

    public void commit(){
        Container newContainer = new Container();
        newContainer.setAmount(view.getAmount());
        ItemKK item = model.getItemByKbNumber(view.getKbNumber());
        if(item==null){
            item = model.getItemByKkNumber(view.getKkNumber());
            if(item==null){
                view.noItemFound();
                return;
            }
        }
        newContainer.setItem(item);
        newContainer.setPayed(false);
        newContainer.setAmount(view.getAmount());
        newContainer.setUser(model.getUser());
        model.addContainer(newContainer);
        refreshUnpaidContainers();
    }

    public void remove() {
        refreshUnpaidContainers();
    }

    void exit() {
        model.saveChanges();
    }
}
