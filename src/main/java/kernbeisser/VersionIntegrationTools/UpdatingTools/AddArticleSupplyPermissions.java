package kernbeisser.VersionIntegrationTools.UpdatingTools;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
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
        .setParameter("template", "ARTICLE_PRODUCER_")
        .setParameter("new", "ARTICLE_CATALOGPRICEFACTOR_")
        .executeUpdate();
    em.createNativeQuery(VersionUpdatingTool.insertRwKeySetPairQuery)
        .setParameter("template", "ARTICLE_PRODUCER_")
        .setParameter("new", "ARTICLE_LABELCOUNT_")
        .executeUpdate();
    em.createNativeQuery(VersionUpdatingTool.insertRwKeySetPairQuery)
        .setParameter("template", "ARTICLE_PRODUCER_")
        .setParameter("new", "ARTICLE_LABELPERUNIT_")
        .executeUpdate();
    em.createNativeQuery(
            "UPDATE Article Set catalogPriceFactor = 1, labelCount = 1, labelperunit = 0")
        .executeUpdate();
  }
}
