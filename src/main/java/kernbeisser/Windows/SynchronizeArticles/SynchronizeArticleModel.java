package kernbeisser.Windows.SynchronizeArticles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.ArticleBase;
import kernbeisser.DBEntities.ArticleKornkraft;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public class SynchronizeArticleModel implements IModel<SynchronizeArticleController> {

  private final Collection<ArticleDifference<?>> allDifferences = loadAllDifferences();

  private Collection<ArticleDifference<?>> loadAllDifferences() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    HashMap<Integer, ArticleKornkraft> kornkraftHashMap = new HashMap<>();
    ArticleKornkraft.getAll("where synchronised = false")
        .forEach(e -> kornkraftHashMap.put(e.getSuppliersItemNumber(), e));
    ArrayList<ArticleBase> a = new ArrayList<>();
    ArrayList<ArticleBase> b = new ArrayList<>();
    em.createQuery("select a from Article a where a.supplier.shortName = 'KK'", Article.class)
        .getResultList()
        .forEach(
            e -> {
              ArticleKornkraft kornkraft = kornkraftHashMap.get(e.getSuppliersItemNumber());
              if (kornkraft != null) {
                a.add(e);
                b.add(kornkraft);
              }
            });
    ArrayList<ArticleDifference<?>> out = new ArrayList<>();
    out.addAll(createDifference("Preis", ArticleBase::getNetPrice, ArticleBase::setNetPrice, a, b));
    out.addAll(
        createDifference(
            "Gebindegröße", ArticleBase::getContainerSize, ArticleBase::setContainerSize, a, b));
    out.addAll(
        createDifference(
            "Einzelpfand", ArticleBase::getSingleDeposit, ArticleBase::setSingleDeposit, a, b));
    out.addAll(
        createDifference(
            "Kistenpfand",
            ArticleBase::getContainerDeposit,
            ArticleBase::setContainerDeposit,
            a,
            b));
    out.addAll(
        createDifference("Packungsmenge", ArticleBase::getAmount, ArticleBase::setAmount, a, b));
    return out;
  }

  Collection<ArticleDifference<?>> getAllDifferences() {
    return allDifferences;
  }

  private <T> Collection<ArticleDifference<T>> createDifference(
      String name,
      Function<ArticleBase, T> getValue,
      BiConsumer<ArticleBase, T> setValue,
      List<ArticleBase> a,
      List<ArticleBase> b) {
    ArrayList<ArticleDifference<T>> out = new ArrayList<>();
    for (int i = 0; i < a.size(); i++) {
      if (!getValue.apply(a.get(i)).equals(getValue.apply(b.get(i)))) {
        out.add(new ArticleDifference<>(a.get(i), b.get(i), getValue, setValue, name));
      }
    }
    return out;
  }
}
