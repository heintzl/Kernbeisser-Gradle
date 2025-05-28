package kernbeisser.Windows.SynchronizeArticles;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Article_;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.DBEntities.SurchargeGroup_;
import kernbeisser.Tasks.Catalog.CatalogDataInterpreter;
import kernbeisser.Tasks.Catalog.Merge.ArticleMerge;
import kernbeisser.Tasks.Catalog.Merge.CatalogMergeSession;
import kernbeisser.Tasks.DTO.Catalog;
import kernbeisser.Tasks.DTO.KornkraftGroup;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

import static kernbeisser.Useful.Constants.KK_SUPPLIER;

public class SynchronizeArticleModel implements IModel<SynchronizeArticleController> {

  private CatalogMergeSession mergeSession;

  private void refresh(
      HashMap<KornkraftGroup, SurchargeGroup> surchargeGroupHashMap, EntityManager em) {
    Map<String, SurchargeGroup> nameRef =
        QueryBuilder.selectAll(SurchargeGroup.class)
            .where(SurchargeGroup_.supplier.eq(KK_SUPPLIER))
            .getResultMap(em, SurchargeGroup::pathString, sg -> sg);
    surchargeGroupHashMap.replaceAll((a, b) -> nameRef.get(b.pathString()));
  }

  public void load(Collection<String> source) {
    mergeSession = new CatalogMergeSession(source);
  }

  public boolean isCatalogLoaded() {
    return mergeSession != null;
  }

  void setProductGroups(Stream<String> source) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Catalog catalog = Catalog.read(source);
    HashMap<KornkraftGroup, SurchargeGroup> surchargeGroupHashMap =
        CatalogDataInterpreter.extractSurchargeGroups(
            CatalogDataInterpreter.extractGroupsTree(catalog));
    refresh(surchargeGroupHashMap, em);
    HashMap<Long, SurchargeGroup> kornkraftGroupHashMap =
        CatalogDataInterpreter.createNumberRefMap(catalog, surchargeGroupHashMap);
    List<Article> articleBases =
        QueryBuilder.selectAll(Article.class)
            .where(Article_.supplier.eq(KK_SUPPLIER))
            .getResultList(em);
    CatalogDataInterpreter.linkArticles(articleBases, kornkraftGroupHashMap);
    CatalogDataInterpreter.autoLinkArticle(
        articleBases, KK_SUPPLIER.getOrPersistDefaultSurchargeGroup(em));
    articleBases.forEach(em::persist);
    em.flush();
  }

  public Collection<ArticleMerge> getAllDiffs() {
    return mergeSession.getArticleMerges();
  }

  public void pushToDB() {
    mergeSession.pushToDB();
  }

  public void checkDiffs() {
    mergeSession.checkMergeStatus();
  }

  public void kill() {
    if (mergeSession != null) mergeSession.kill();
    mergeSession = null;
  }
}
