package kernbeisser.VersionIntegrationTools.UpdatingTools;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.VersionIntegrationTools.VersionUpdatingTool;
import lombok.Cleanup;

public class RemoveOfferFromDB implements VersionUpdatingTool {
  @Override
  public void runIntegration() {
    String[] indexes = {"UK7hv9yi2e7hp2m3e0lvcoyia1y",
            "UK9f8dlcsg8r6ikdo04l5q0tbby",
            "UK1g2p2t7n54x9ikt6o9i7h99vy",
            "UK_egie16sjdiogwvu41gbuuk75t",
    "IX_article_barcode",
    "IX_article_kbNumber"};
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    em.createNativeQuery("DROP TABLE IF EXISTS Offer;").executeUpdate();
    for (String ix : indexes) {
      em.createNativeQuery("ALTER TABLE Article DROP INDEX IF EXISTS %s;".formatted(ix)).executeUpdate();
    }
  }
}
