package kernbeisser.Windows.EditSurchargeGroups;

import java.util.Collection;
import javax.persistence.EntityManager;
import kernbeisser.CustomComponents.ObjectTree.Node;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public class EditSurchargeGroupModel implements IModel<EditSurchargeGroupController> {

  Collection<Supplier> getAllSuppliers() {
    return Supplier.getAll(null);
  }

  Collection<Node<SurchargeGroup>> getSurchargeGroupTree(Supplier supplier) {
    return Node.createMappingNode(getAllFromSupplier(supplier), SurchargeGroup::getParent);
  }

  Collection<SurchargeGroup> getAllFromSupplier(Supplier supplier) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    return em.createQuery(
            "select s from SurchargeGroup s where s.supplier = :s order by s.name asc",
            SurchargeGroup.class)
        .setParameter("s", supplier)
        .getResultList();
  }
}
