package kernbeisser.Windows.EditSurchargeGroups;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.Collection;
import java.util.List;
import kernbeisser.CustomComponents.ObjectTree.Node;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.DBEntities.Article_;
import kernbeisser.DBEntities.SurchargeGroup_;
import kernbeisser.Tasks.Catalog.CatalogDataInterpreter;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public class EditSurchargeGroupModel implements IModel<EditSurchargeGroupController> {

  Collection<Supplier> getAllSuppliers() {
    return Tools.getAll(Supplier.class);
  }

  Node<SurchargeGroup> getSurchargeGroupTree(Supplier supplier) {
    return SurchargeGroup.allSurchargeGroupsFromSupplierAsNode(supplier);
  }

  Collection<SurchargeGroup> getAllFromSupplier(Supplier supplier) {
    return QueryBuilder.selectAll(SurchargeGroup.class)
        .where(SurchargeGroup_.surcharge.eq(supplier))
        .getResultList();
  }

  // tries to find another
  public void autoLinkAllInSurchargeGroup(int surchargeGroupId) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    SurchargeGroup surchargeGroup = em.find(SurchargeGroup.class, surchargeGroupId);
    List<Article> allArticles =
        QueryBuilder.selectAll(Article.class)
            .where(Article_.supplier.eq(surchargeGroup.getSupplier()))
            .getResultList(em);
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
