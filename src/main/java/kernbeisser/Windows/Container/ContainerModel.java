package kernbeisser.Windows.Container;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.*;
import kernbeisser.Windows.Model;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.Collection;

public class ContainerModel implements Model {
    private final Collection<Container> newContainers = new ArrayList<>();
    private final User user;

    ContainerModel(User user) {
        this.user = user;
    }

    Collection<Container> getOldContainers() {
        return Container.getAll("where payed = false");
    }

    Collection<Container> getLastContainers() {
        EntityManager em = DBConnection.getEntityManager();
        Collection<Container> out = em.createQuery("select c from Container c order by createDate desc",
                                                   Container.class).getResultList();
        em.close();
        return out;
    }

    Collection<Container> getNewContainers() {
        return newContainers;
    }

    void addContainer(Container container) {
        newContainers.add(container);
    }

    void saveChanges() {
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        newContainers.forEach(em::persist);
        em.flush();
        et.commit();
        em.close();
    }

    ItemKK getItemByKbNumber(int kbNumber) {
        return ItemKK.getByKbNumber(kbNumber);
    }

    ItemKK getItemByKkNumber(int kkNumber) {
        return ItemKK.getByKkNumber(kkNumber);
    }

    public User getUser() {
        return user;
    }

    void removeNew(Container unpaidOrder) {
        newContainers.remove(unpaidOrder);
    }
}
