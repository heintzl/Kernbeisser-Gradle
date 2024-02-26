package kernbeisser.VersionIntegrationTools.UpdatingTools;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.VersionIntegrationTools.VersionUpdatingTool;
import lombok.Cleanup;
import lombok.SneakyThrows;

public class AddCatalogImportPermission implements VersionUpdatingTool {

  @SneakyThrows
  @Override
  public void runIntegration() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    em.createNativeQuery(VersionUpdatingTool.insertSimpleKeySetQuery)
        .setParameter("template", "ACTION_OPEN_SYNCHRONISE_ARTICLE_WINDOW")
        .setParameter("new", "ACTION_OPEN_CATALOG_IMPORT")
        .executeUpdate();
  }
}
