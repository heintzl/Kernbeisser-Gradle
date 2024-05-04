package kernbeisser.VersionIntegrationTools.UpdatingTools;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.VersionIntegrationTools.VersionUpdatingTool;
import lombok.Cleanup;

public class PopulateSeqNo implements VersionUpdatingTool {
  @Override
  public void runIntegration() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    em.createNativeQuery(
            "UPDATE Transaction t1 SET seqNo = (SELECT count(*) FROM Transaction t2 WHERE t2.id < t1.id)")
        .executeUpdate();
    em.createNativeQuery("UPDATE Purchase SET bonNo = id").executeUpdate();
  }
}
