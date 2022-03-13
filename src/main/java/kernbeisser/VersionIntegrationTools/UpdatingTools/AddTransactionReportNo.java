package kernbeisser.VersionIntegrationTools.UpdatingTools;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.VersionIntegrationTools.VersionUpdatingTool;
import lombok.Cleanup;
import lombok.SneakyThrows;

public class AddTransactionReportNo implements VersionUpdatingTool {

  @SneakyThrows
  @Override
  public void runIntegration() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    em.createNativeQuery(VersionUpdatingTool.insertRwKeySetPairQuery)
        .setParameter("template", "TRANSACTION_ID_")
        .setParameter("new", "TRANSACTION_ACCOUNTINGREPORTNO_")
        .executeUpdate();
  }
}
