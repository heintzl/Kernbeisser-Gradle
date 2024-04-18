package kernbeisser.VersionIntegrationTools.UpdatingTools;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.VersionIntegrationTools.VersionUpdatingTool;
import lombok.Cleanup;
import lombok.SneakyThrows;

public class AddOnShoppingMaskCheckoutPermission implements VersionUpdatingTool {

  @SneakyThrows
  @Override
  public void runIntegration() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    em.createNativeQuery(VersionUpdatingTool.insertSimpleKeySetQuery)
        .setParameter("template", "POST_ON_SALE_SESSION_CLOSE")
        .setParameter("new", "POST_ON_SHOPPINGMASK_CHECKOUT")
        .executeUpdate();
  }
}
