package kernbeisser.StartUp.DataImport;

import static kernbeisser.Useful.Users.generateUserRelatedToken;

import at.favre.lib.crypto.bcrypt.BCrypt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Job;
import kernbeisser.DBEntities.Offer;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Enums.PermissionConstants;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.TransactionType;
import kernbeisser.Exeptions.InvalidTransactionException;
import kernbeisser.Forms.ObjectForm.Exceptions.CannotParseException;
import kernbeisser.Main;
import kernbeisser.Security.Access.Access;
import kernbeisser.Security.Access.AccessManager;
import kernbeisser.Tasks.Articles;
import kernbeisser.Tasks.Catalog.CatalogDataInterpreter;
import kernbeisser.Tasks.Catalog.Merge.CatalogMergeSession;
import kernbeisser.Tasks.DTO.Catalog;
import kernbeisser.Tasks.Users;
import kernbeisser.Useful.ErrorCollector;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public class DataImportModel implements IModel<DataImportController> {

  void createAdmin(String password) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    User user = new User();
    Access.getExceptions().put(user, AccessManager.NO_ACCESS_CHECKING);
    user.setFirstName("System");
    user.setSurname("Admin");
    user.setUsername("Admin");
    user.setPassword(
        BCrypt.withDefaults()
            .hashToString(Setting.HASH_COSTS.getIntValue(), password.toCharArray()));
    user.getPermissions().add(PermissionConstants.ADMIN.getPermission());
    user.setUserGroup(new UserGroup());
    em.persist(user.getUserGroup());
    em.persist(user);
    em.flush();
    Access.getExceptions().remove(user);
  }

  void parsePriceLists(Stream<String> f, Consumer<Integer> progress) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    HashMap<String, PriceList> priceLists = new HashMap<>();
    f.forEach(
        e -> {
          String[] columns = e.split(";");
          PriceList pl = new PriceList();
          pl.setName(columns[0]);
          pl.setSuperPriceList(priceLists.get(columns[1]));
          priceLists.put(pl.getName(), pl);
        });
    progress.accept(3);
    priceLists.values().forEach(em::persist);
    em.flush();
    progress.accept(4);
  }

  void parseSuppliers(Stream<String> f, Consumer<Integer> progress) {
    EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    f.forEach(
        e -> {
          String[] columns = e.replace("NULL", "").split(";");
          Supplier supplier = new Supplier();
          supplier.setShortName(columns[0]);
          supplier.setName(columns[1]);
          supplier.setPhoneNumber(columns[2]);
          supplier.setEmail(columns[3]);
          supplier.setFax(columns[4]);
          supplier.setStreet(columns[5]);
          supplier.setLocation(columns[6]);
          supplier.setKeeper(columns[7]);
          supplier.setDefaultSurcharge(Integer.parseInt(columns[8]) / 100.);
          SurchargeGroup surchargeGroup = new SurchargeGroup();
          surchargeGroup.setSupplier(supplier);
          surchargeGroup.setName(supplier.getName() + " Standard Aufschlag");
          em.persist(supplier);
        });
    progress.accept(2);
    em.flush();
    et.commit();
  }

  void parseUsers(Stream<String> f, Consumer<Integer> progress) {
    HashSet<String> usernames = new HashSet<>();
    HashMap<String, Job> jobs = new HashMap<>();
    Job.getAll(null).forEach(e -> jobs.put(e.getName(), e));
    final boolean relatedPassword = Setting.GENERATE_PASSWORD_RELATED_TO_USERNAME.getBooleanValue();
    Permission importPermission = PermissionConstants.IMPORT.getPermission();
    Permission keyPermission = PermissionConstants.KEY_PERMISSION.getPermission();
    User kernbeisser = User.getKernbeisserUser();
    BCrypt.Hasher hasher = BCrypt.withDefaults();
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    f.forEach(
        l -> {
          String[] rawUserData = l.split(";");

          User[] users = Users.parse(rawUserData, usernames, jobs);

          UserGroup userGroup = Users.getUserGroup(rawUserData);

          users[0].setUserGroup(userGroup);
          users[1].setUserGroup(userGroup);

          users[0].setPassword(
              hasher.hashToString(
                  4,
                  relatedPassword
                      ? generateUserRelatedToken(users[0].getUsername()).toCharArray()
                      : "start".toCharArray()));
          users[1].setPassword(
              hasher.hashToString(
                  4,
                  relatedPassword
                      ? generateUserRelatedToken(users[1].getUsername()).toCharArray()
                      : "start".toCharArray()));

          em.persist(userGroup);

          users[0].getPermissions().add(importPermission);
          users[1].getPermissions().add(importPermission);

          if (users[0].getKernbeisserKey() != -1) {
            users[0].getPermissions().add(keyPermission);
          }

          em.persist(users[0]);

          if (!users[1].getFirstName().equals("")) {
            em.persist(users[1]);
          }
          try {
            Transaction.doTransaction(
                em,
                kernbeisser,
                users[0],
                Users.getValue(rawUserData),
                TransactionType.INITIALIZE,
                "Übertrag des Guthabens des alten Kernbeisser Programms");
          } catch (InvalidTransactionException e) {
            throw new RuntimeException(e);
          }
        });
    em.flush();
    progress.accept(4);
  }

  void parseArticle(
      Stream<String> f,
      Collection<String> kornkraftCatalog,
      Stream<String> productsJson,
      Consumer<Integer> progress) {
    readArticles(f);
    readCatalog(kornkraftCatalog);
    setProductGroups(productsJson);
  }

  private void setProductGroups(Stream<String> productGroups) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Catalog catalog = Catalog.read(productGroups);
    HashMap<Long, SurchargeGroup> surchargeGroupHashMap =
        CatalogDataInterpreter.createNumberRefMap(
            catalog,
            CatalogDataInterpreter.extractSurchargeGroups(
                CatalogDataInterpreter.extractGroupsTree(catalog), em));
    List<Article> articles =
        em.createQuery("select a from Article a where supplier = :s", Article.class)
            .setParameter("s", Supplier.getKKSupplier())
            .getResultList();
    CatalogDataInterpreter.linkArticles(articles, surchargeGroupHashMap);
    CatalogDataInterpreter.autoLinkArticle(
        articles, Supplier.getKKSupplier().getOrPersistDefaultSurchargeGroup(em));
    articles.forEach(em::persist);
    em.flush();
  }

  private void readCatalog(Collection<String> kornkraftCatalog) {
    CatalogMergeSession mergeSession = new CatalogMergeSession(kornkraftCatalog);
    mergeSession.resolveAllFor(true);
    mergeSession.pushToDB();
  }

  private void readArticles(Stream<String> f) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    HashSet<Long> barcode = new HashSet<>(5000);
    HashSet<String> names = new HashSet<>();
    HashMap<String, PriceList> priceListHashMap = new HashMap<>();
    HashMap<String, Supplier> suppliers = new HashMap<>();
    HashMap<Supplier, SurchargeGroup> defaultGroup = new HashMap<>();
    HashMap<Article, Collection<Offer>> articleCollectionHashMap = new HashMap<>(5000);
    Tools.getAllUnProxy(Supplier.class).forEach(e -> suppliers.put(e.getShortName(), e));
    Tools.getAllUnProxy(PriceList.class).forEach(e -> priceListHashMap.put(e.getName(), e));
    suppliers.values().forEach(e -> defaultGroup.put(e, e.getOrPersistDefaultSurchargeGroup(em)));
    ErrorCollector errorCollector = new ErrorCollector();
    f.forEach(
        e -> {
          String[] columns = e.split(";");
          try {
            articleCollectionHashMap.put(
                Articles.parse(columns, barcode, names, suppliers, defaultGroup, priceListHashMap),
                Articles.extractOffers(columns));
          } catch (CannotParseException ex) {
            errorCollector.collect(ex);
          }
        });
    Main.logger.warn("Ignored " + errorCollector.count() + " articles errors:");
    errorCollector.log();
    ArrayList<Article> articles = new ArrayList<>(articleCollectionHashMap.keySet());
    Tools.group(articles, Article::getSupplier)
        .forEach(
            (v, k) ->
                Tools.fillUniqueFieldWithNextAvailable(
                    k,
                    Article::getSuppliersItemNumber,
                    (o, a) -> {
                      o.setSuppliersItemNumber(a);
                      o.setVerified(false);
                    },
                    e -> e + 1));
    articles.forEach(em::persist);
    em.flush();
  }

  void parseJobs(Stream<String> f, Consumer<Integer> progress) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    f.forEach(
        e -> {
          String[] columns = e.split(";");
          Job job = new Job();
          job.setName(columns[0]);
          job.setDescription(columns[1]);
          em.persist(job);
        });
    em.flush();
    progress.accept(2);
  }
}
