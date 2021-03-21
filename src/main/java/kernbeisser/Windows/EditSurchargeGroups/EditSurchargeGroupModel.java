package kernbeisser.Windows.EditSurchargeGroups;

import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.CustomComponents.ObjectTree.Node;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.Tasks.Catalog.CatalogDataInterpreter;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public class EditSurchargeGroupModel implements IModel<EditSurchargeGroupController> {

  Collection<Supplier> getAllSuppliers() {
    return Supplier.getAll(null);
  }

  Node<SurchargeGroup> getSurchargeGroupTree(Supplier supplier) {
    return SurchargeGroup.asMappedNode(supplier);
  }

  Collection<SurchargeGroup> getAllFromSupplier(Supplier supplier) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "select s from SurchargeGroup s where s.supplier = :s order by s.name asc",
            SurchargeGroup.class)
        .setParameter("s", supplier)
        .getResultList();
  }

  // tries to find another
  public void autoLinkAllInSurchargeGroup(int surchargeGroupId) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    SurchargeGroup surchargeGroup = em.find(SurchargeGroup.class, surchargeGroupId);
    List<Article> allArticles =
        em.createQuery("select a from Article a where supplier = :sid", Article.class)
            .setParameter("sid", surchargeGroup.getSupplier())
            .getResultList();
    if (!allArticles.stream().allMatch(e -> e.getSurchargeGroup().equals(surchargeGroup))) {
      CatalogDataInterpreter.autoLinkArticle(allArticles, surchargeGroup);
      allArticles.forEach(em::persist);
      em.flush();
      et.commit();
    } else {
      et.rollback();
      throw new UnsupportedOperationException(
          "the supplier: "
              + surchargeGroup.getSupplier()
              + " doesn't have any optional surcharge groups");
    }
  }
}
