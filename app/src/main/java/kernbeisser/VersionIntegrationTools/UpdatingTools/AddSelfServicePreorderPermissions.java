package kernbeisser.VersionIntegrationTools.UpdatingTools;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PreOrderCreator;
import kernbeisser.VersionIntegrationTools.VersionUpdatingTool;
import lombok.Cleanup;

public class AddSelfServicePreorderPermissions implements VersionUpdatingTool {

  @Override
  public void runIntegration() {
    this.updatePermissionKeyset();
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    em.createNativeQuery(VersionUpdatingTool.insertRwKeySetPairQuery)
        .setParameter("template", "CONTAINER_ID_")
        .setParameter("new", "CONTAINER_ALTERNATIVE_ITEM_")
        .executeUpdate();
    em.createNativeQuery(VersionUpdatingTool.insertRwKeySetPairQuery)
        .setParameter("template", "CONTAINER_ID_")
        .setParameter("new", "CONTAINER_LAST_WEEK_OF_DELIVERY_")
        .executeUpdate();
    em.createNativeQuery(VersionUpdatingTool.insertRwKeySetPairQuery)
        .setParameter("template", "CONTAINER_ID_")
        .setParameter("new", "CONTAINER_COMMENT_")
        .executeUpdate();
    em.createNativeQuery(VersionUpdatingTool.insertRwKeySetPairQuery)
        .setParameter("template", "CONTAINER_ID_")
        .setParameter("new", "CONTAINER_CREATION_TYPE_")
        .executeUpdate();
    em.createNativeQuery(VersionUpdatingTool.insertRwKeySetPairQuery)
        .setParameter("template", "CONTAINER_ID_")
        .setParameter("new", "CONTAINER_CREATED_BY_")
        .executeUpdate();
    em.createNativeQuery(
            "UPDATE PreOrder Set CreationType = " + PreOrderCreator.PRODUCT_COORDINATOR.ordinal())
        .executeUpdate();
  }
}
