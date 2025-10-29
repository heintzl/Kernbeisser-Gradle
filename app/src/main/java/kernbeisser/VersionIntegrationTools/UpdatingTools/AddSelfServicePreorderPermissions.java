package kernbeisser.VersionIntegrationTools.UpdatingTools;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
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
        .setParameter("template", "CONTAINER_ID")
        .setParameter("new", "CONTAINER_ALTERNATIVE_ITEM")
        .executeUpdate();
    em.createNativeQuery(VersionUpdatingTool.insertRwKeySetPairQuery)
        .setParameter("template", "CONTAINER_ID")
        .setParameter("new", "CONTAINER_FIRST_WEEK_OF_DELIVERY")
        .executeUpdate();
    em.createNativeQuery(VersionUpdatingTool.insertRwKeySetPairQuery)
        .setParameter("template", "CONTAINER_ID")
        .setParameter("new", "CONTAINER_LAST_WEEK_OF_DELIVERY")
        .executeUpdate();
    em.createNativeQuery(VersionUpdatingTool.insertRwKeySetPairQuery)
        .setParameter("template", "CONTAINER_ID")
        .setParameter("new", "CONTAINER_COMMENT")
        .executeUpdate();
    em.createNativeQuery(VersionUpdatingTool.insertRwKeySetPairQuery)
        .setParameter("template", "CONTAINER_ID")
        .setParameter("new", "CONTAINER_CREATION_TYPE")
        .executeUpdate();
    em.createNativeQuery(VersionUpdatingTool.insertRwKeySetPairQuery)
        .setParameter("template", "CONTAINER_ID")
        .setParameter("new", "CONTAINER_CREATED_BY")
        .executeUpdate();
    em.createNativeQuery("UPDATE PreOrder Set CreationType = 'PRE_ORDER_MANAGER'").executeUpdate();
  }
}
