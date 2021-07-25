package kernbeisser.Windows.SynchronizeArticles;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.Tasks.Catalog.CatalogDataInterpreter;
import kernbeisser.Tasks.Catalog.Merge.ArticleDifference;
import kernbeisser.Tasks.Catalog.Merge.CatalogMergeSession;
import kernbeisser.Tasks.DTO.Catalog;
import kernbeisser.Tasks.DTO.KornkraftGroup;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public class SynchronizeArticleModel implements IModel<SynchronizeArticleController> {

  private CatalogMergeSession mergeSession;

  private void refresh(
      HashMap<KornkraftGroup, SurchargeGroup> surchargeGroupHashMap, EntityManager em) {
    HashMap<String, SurchargeGroup> nameRef = new HashMap<>();
    em.createQuery("select s from SurchargeGroup s where supplier = :s", SurchargeGroup.class)
        .setParameter("s", Supplier.getKKSupplier())
        .getResultStream()
        .forEach(e -> nameRef.put(e.pathString(), e));
    surchargeGroupHashMap.replaceAll((a, b) -> nameRef.get(b.pathString()));
  }

  public void load(Collection<String> source) {
    mergeSession = new CatalogMergeSession(source);
  }

  public boolean isCatalogLoaded() {
    return mergeSession != null;
  }

  public void resolveDifference(ArticleDifference<?> articleDifference, boolean useCurrent) {
    mergeSession.resolveDifference(articleDifference, useCurrent);
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
        em.createQuery("select a from Article a where supplier = :s", Article.class)
            .setParameter("s", Supplier.getKKSupplier())
            .getResultList();
    CatalogDataInterpreter.linkArticles(articleBases, kornkraftGroupHashMap);
    CatalogDataInterpreter.autoLinkArticle(
        articleBases, Supplier.getKKSupplier().getOrPersistDefaultSurchargeGroup(em));
    articleBases.forEach(em::persist);
    em.flush();
  }

  public Collection<ArticleDifference<?>> getAllDiffs() {
    return mergeSession.getDiffs();
  }

  public void resolveAndIgnoreDifference(ArticleDifference<?> selectedObject) {
    mergeSession.resolveAndIgnoreDifference(selectedObject);
  }

  public void pushToDB() {
    mergeSession.pushToDB();
  }

  public void checkDiffs() {
    mergeSession.checkDiffs();
  }

  public boolean hasAvailableDiffs() {
    return mergeSession.diffsAvailable();
  }
}
