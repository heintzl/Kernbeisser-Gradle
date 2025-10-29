package kernbeisser.VersionIntegrationTools.UpdatingTools;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.VersionIntegrationTools.VersionUpdatingTool;
import lombok.Cleanup;
import lombok.SneakyThrows;

public class AddArticleSupplyPermissions implements VersionUpdatingTool {

  @SneakyThrows
  @Override
  public void runIntegration() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    em.createNativeQuery(VersionUpdatingTool.insertRwKeySetPairQuery)
        .setParameter("template", "ARTICLE_PRODUCER")
        .setParameter("new", "ARTICLE_CATALOGPRICEFACTOR")
        .executeUpdate();
    em.createNativeQuery(VersionUpdatingTool.insertRwKeySetPairQuery)
        .setParameter("template", "ARTICLE_PRODUCER")
        .setParameter("new", "ARTICLE_LABELCOUNT")
        .executeUpdate();
    em.createNativeQuery(VersionUpdatingTool.insertRwKeySetPairQuery)
        .setParameter("template", "ARTICLE_PRODUCER")
        .setParameter("new", "ARTICLE_LABELPERUNIT")
        .executeUpdate();
    em.createNativeQuery(
            "UPDATE Article Set catalogPriceFactor = 1, labelCount = 1, labelperunit = 0")
        .executeUpdate();
  }
}
