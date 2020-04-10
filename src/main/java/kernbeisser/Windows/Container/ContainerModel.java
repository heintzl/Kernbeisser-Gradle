package kernbeisser.Windows.Container;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.ArticleKornkraft;
import kernbeisser.DBEntities.Container;
import kernbeisser.DBEntities.User;
import kernbeisser.Windows.Model;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.Collection;

public class ContainerModel implements Model<ContainerController> {
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

    ArticleKornkraft getItemByKbNumber(int kbNumber) {
        return ArticleKornkraft.getByKbNumber(kbNumber);
    }

    ArticleKornkraft getItemByKkNumber(int kkNumber) {
        return ArticleKornkraft.getByKkNumber(kkNumber);
    }

    public User getUser() {
        return user;
    }

    void removeNew(Container unpaidOrder) {
        newContainers.remove(unpaidOrder);
    }
}
