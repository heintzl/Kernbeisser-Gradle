package kernbeisser.Windows.SynchronizeArticles;

import static kernbeisser.Tasks.Catalog.Catalog.parseArticle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.IgnoredDifference;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Main;
import kernbeisser.Tasks.Catalog.CatalogDataInterpreter;
import kernbeisser.Tasks.DTO.Catalog;
import kernbeisser.Tasks.DTO.KornkraftGroup;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import org.hibernate.Session;

public class SynchronizeArticleModel implements IModel<SynchronizeArticleController> {

  private final EntityManager em = DBConnection.getEntityManager();
  private EntityTransaction et = em.getTransaction();
  {
    et.begin();
  }

  private Set<ArticleDifference<?>> diffs;
  private Collection<Article> collected;

  private final Collection<IgnoredDifference> inserted = new ArrayList<>();

  private HashMap<Integer, Collection<IgnoredDifference>> loadIgnoreDifferences() {
    Collection<IgnoredDifference> queryResult = em.createQuery("select i from IgnoredDifference i",IgnoredDifference.class).getResultList();
    HashMap<Integer,Collection<IgnoredDifference>> out = new HashMap<>(queryResult.size());
    queryResult.forEach(e -> out.computeIfAbsent(e.getArticle().getSuppliersItemNumber(),b -> new ArrayList<>()));
    queryResult.forEach(e -> out.get(e.getArticle().getSuppliersItemNumber()).add(e));
    return out;
  }


  public SynchronizeArticleModel(){}

  private void refresh(
      HashMap<KornkraftGroup, SurchargeGroup> surchargeGroupHashMap, EntityManager em) {
    HashMap<String, SurchargeGroup> nameRef = new HashMap<>();
    em.createQuery("select s from SurchargeGroup s where supplier = :s", SurchargeGroup.class)
        .setParameter("s", Supplier.getKKSupplier())
        .getResultStream()
        .forEach(e -> nameRef.put(e.pathString(), e));
    surchargeGroupHashMap.replaceAll((a, b) -> nameRef.get(b.pathString()));
  }

  private HashMap<Integer,Article> loadCurrentState(){
    List<Article> result = em.createQuery("select a from Article a where supplier = :s",Article.class).setParameter("s",Supplier.getKKSupplier()).getResultList();
    HashMap<Integer,Article> out = new HashMap<>(result.size());
    result.forEach(a -> out.put(a.getSuppliersItemNumber(),a));
    return out;
  }

  public void load(Stream<String> source){
    collected = new ArrayList<>();
    diffs = loadSource(source,MappedDifferences.values());
  }

  private Set<ArticleDifference<?>> loadSource(Stream<String> source,MappedDifferences ... differences) {
    Supplier kkSupplier = Supplier.getKKSupplier();
    SurchargeGroup undefined = SurchargeGroup.undefined();
    PriceList coveredIntake = PriceList.getCoveredIntakePriceList();

    HashMap<Integer, Article> currentState = loadCurrentState();
    HashMap<Integer, Collection<IgnoredDifference>> currentIgnoredDifferences = loadIgnoreDifferences();

    HashSet<ArticleDifference<?>> out = new HashSet<>();
    HashMap<Integer,Double> deposit = new HashMap<>(10000);
    HashSet<Integer> suppliersItemNumbers = new HashSet<>();
    source.skip(1).map(e -> e.split(";")).peek(e -> {
      try {
        deposit.put(Integer.parseInt(e[0]),Double.parseDouble(e[37].replace(",", ".")));
      }catch (NumberFormatException ignored){}
    }).forEach(columns -> {
      try {
        int suppliersItemNumber = Integer.parseInt(columns[0]);
        if (!suppliersItemNumbers.add(suppliersItemNumber)) return;
        Article current = currentState.get(suppliersItemNumber);
        Article articleSocket;
        if (current == null) {
          articleSocket = createNewBase(kkSupplier, coveredIntake, undefined);
        } else {
          articleSocket = Tools.clone(current);
        }
        Article article = parseArticle(articleSocket, columns);
        Double singleDepositValue = deposit.get((int)article.getSingleDeposit());
        Double containerDepositValue = deposit.get((int)article.getContainerDeposit());
        article.setSingleDeposit(singleDepositValue == null ? 0 : singleDepositValue);
        article.setContainerDeposit(containerDepositValue == null ? 0 : containerDepositValue);
        collected.add(article);
        if (current != null) {
          Collection<IgnoredDifference> ignoredDifferences = currentIgnoredDifferences
              .get(current.getSuppliersItemNumber());
          for (MappedDifferences difference : differences) {
            ArticleDifference<?> articleDifference = new ArticleDifference<>(difference, current,
                article);
            if (!difference.equal(current, article) && (ignoredDifferences == null || !ignoredDifferences
                .contains(IgnoredDifference.from(articleDifference))))
              out.add(articleDifference);
          }
        }

      } catch (CannotParseException | NumberFormatException e) {
        Main.logger.warn("Ignored ArticleKornKraft because " + e.getMessage());
      }
    });

    return out;
  }

  public void checkDiffs(){
    if(collected == null) throw new UnsupportedOperationException("load catalog first");
    if(diffs.size() != 0)throw new UnsupportedOperationException("merge diffs fist");
  }

  public boolean isCatalogLoaded(){
    return collected != null;
  }

  public void pushToDB(){
    checkDiffs();
    Session session = em.unwrap(Session.class);
    HashSet<Integer> kbNumbers = new HashSet<>(em.createQuery("select a.kbNumber" + " from Article a", Integer.class).getResultList());
    HashSet<Integer> inUse = new HashSet<>();
    for (Article article : collected) {
      if(!inUse.add(article.getKbNumber())){
        int out = article.getKbNumber()+1;
        while (kbNumbers.contains(out)){
          out++;
        }
        kbNumbers.add(out);
        inUse.add(out);
        article.setKbNumber(out);
      }else kbNumbers.add(article.getKbNumber());
      session.merge(article);
    }
    em.flush();
    for (IgnoredDifference ignoredDifference : inserted) {
      em.persist(ignoredDifference);
    }
    em.flush();
    et.commit();
  }

  public void resolveDifference(ArticleDifference<?> articleDifference,boolean useCurrent){
    diffs.remove(articleDifference);
    if(useCurrent)articleDifference.pushCurrentIntoNew();
  }

  public void resolveAllFor(boolean useCurrent){
    if(useCurrent)
    diffs.forEach(ArticleDifference::pushCurrentIntoNew);
    diffs.clear();
  }

  public void resolveAndIgnoreDifference(ArticleDifference<?> articleDifference){
    IgnoredDifference ignoredDifference = IgnoredDifference.from(articleDifference);
    resolveDifference(articleDifference,true);
    inserted.add(ignoredDifference);
  }

  private Article createNewBase(Supplier supplier, PriceList priceList,SurchargeGroup sg){
    Article out = new Article();
    out.setVerified(false);
    out.setSupplier(supplier);
    out.setWeighable(false);
    out.setSurchargeGroup(sg);
    out.setPriceList(priceList);
    return out;
  }

  void setProductGroups(Stream<String> source) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
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
    CatalogDataInterpreter.autoLinkArticle(articleBases);
    articleBases.forEach(em::persist);
    em.flush();
    et.commit();
  }

  public Collection<ArticleDifference<?>> getAllDiffs() {
    return diffs;
  }
}
