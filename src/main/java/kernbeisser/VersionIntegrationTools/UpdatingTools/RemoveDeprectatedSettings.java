package kernbeisser.VersionIntegrationTools.UpdatingTools;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.VersionIntegrationTools.VersionUpdatingTool;
import lombok.Cleanup;

public class RemoveDeprectatedSettings implements VersionUpdatingTool {
  @Override
  public void runIntegration() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    em.createNativeQuery("DELETE FROM SettingValue WHERE setting = 'DB_VERSION'").executeUpdate();
    em.createNativeQuery(
            "UPDATE SettingValue Set setting = 'LAST_PRINTED_TRANSACTION_ID' WHERE setting = 'LAST_PRINTED_BON_NO'")
        .executeUpdate();
  }
}
