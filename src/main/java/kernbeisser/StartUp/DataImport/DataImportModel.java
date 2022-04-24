package kernbeisser.StartUp.DataImport;

import static kernbeisser.Useful.Users.generateUserRelatedToken;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.opencsv.CSVWriterBuilder;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
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
import kernbeisser.Exeptions.MissingFullMemberException;
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
import lombok.var;
import org.jetbrains.annotations.NotNull;

public class DataImportModel implements IModel<DataImportController> {

  public void createAdmin(String password) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    User user = new User();
    Access.putException(user, AccessManager.NO_ACCESS_CHECKING);
    user.setFirstName("System");
    user.setSurname("Admin");
    user.setUsername("Admin");
    user.setPassword(
        BCrypt.withDefaults()
            .hashToString(Setting.HASH_COSTS.getIntValue(), password.toCharArray()));
    user.getPermissions().add(PermissionConstants.ADMIN.getPermission());
    user.setNewUserGroup(em);
    em.persist(user);
    em.flush();
    Access.removeException(user);
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
          PriceList pl = new PriceList(columns[0]);
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

  void parseUsers(
      Stream<String> f, Consumer<Integer> progress, boolean generateUserRelatedPasswords) {
    HashMap<String, User> importedUsers = new HashMap<>();
    HashSet<String> userNames = new HashSet<>();
    HashMap<String, Job> jobs = new HashMap<>();
    HashMap<User, Instant> userCreateDates = new HashMap<>();
    Job.getAll(null).forEach(e -> jobs.put(e.getName(), e));
    Permission importPermission = PermissionConstants.IMPORT.getPermission();
    Permission keyPermission = PermissionConstants.KEY_PERMISSION.getPermission();
    Permission fullMemberPermission = PermissionConstants.FULL_MEMBER.getPermission();
    User kernbeisser = User.getKernbeisserUser();
    BCrypt.Hasher hasher = BCrypt.withDefaults();
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    f.forEach(
        l -> {
          String[] rawUserData = l.split(";");

          User[] users = Users.parse(rawUserData, userNames, jobs);
          String userFullname = users[0].getFullName();
          UserGroup userGroup = Users.getUserGroup(rawUserData);
          User transactionUser = users[0];
          if (importedUsers.containsKey(userFullname)) {
            User existingUser = importedUsers.get(userFullname);
            transactionUser = existingUser;
            if (existingUser.isPrimary()) {
              Main.logger.warn("Ignored duplicate primary user " + userFullname);
            } else {
              userGroup = existingUser.getUserGroup();
              existingUser.setPrimary(true);
              existingUser.setPhoneNumber1(users[0].getPhoneNumber1());
              existingUser.setPhoneNumber2(users[0].getPhoneNumber2());
              existingUser.setEmail(users[0].getEmail());
              existingUser.setJobs(users[0].getJobs());
              existingUser.setEmployee(users[0].isEmployee());
              existingUser.getPermissions().add(fullMemberPermission);
              if (users[0].getKernbeisserKey() != -1) {
                existingUser.setKernbeisserKey(users[0].getKernbeisserKey());
                existingUser.getPermissions().add(keyPermission);
              }
              em.persist(existingUser);
            }

          } else {
            try {
              users[0].setUserGroup(userGroup);
            } catch (MissingFullMemberException e) {
              Main.logger.warn(e);
              userGroup = users[0].setNewUserGroup();
            }
            em.persist(userGroup);
            users[0].setPassword(
                hasher.hashToString(
                    4,
                    generateUserRelatedPasswords
                        ? generateUserRelatedToken(users[0].getUsername()).toCharArray()
                        : "start".toCharArray()));
            users[0].setForcePasswordChange(true);
            users[0].getPermissions().add(importPermission);
            if (users[0].getKernbeisserKey() != -1) {
              users[0].getPermissions().add(keyPermission);
            }
            em.persist(users[0]);
            Optional<Instant> createDate =
                Optional.ofNullable(
                    rawUserData[30].equals("?")
                        ? Instant.EPOCH
                        : LocalDate.parse(
                                rawUserData[30], DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant());
            em.createNativeQuery("UPDATE User SET createDate = :d WHERE id = :uid")
                .setParameter("uid", users[0].getId())
                .setParameter("d", createDate.orElse(Instant.EPOCH))
                .executeUpdate();

            importedUsers.put(userFullname, users[0]);
          }

          double userValue = Users.getValue(rawUserData);
          if (userValue != 0.0) {
            try {
              Transaction.doTransaction(
                  em,
                  kernbeisser,
                  transactionUser,
                  userValue,
                  TransactionType.INITIALIZE,
                  "Übertrag des Guthabens des alten Kernbeisser Programms");
            } catch (InvalidTransactionException e) {
              throw new RuntimeException(e);
            }
          }

          userFullname = users[1].getFullName();
          if (importedUsers.containsKey(userFullname)) {
            User existingUser = importedUsers.get(userFullname);
            if (existingUser.isPrimary()) {
              try {
                Users.switchUserGroup(existingUser.getId(), users[0].getUserGroup().getId());
              } catch (MissingFullMemberException e) {
                Main.logger.warn(
                    "Unsuccessfully tried to put user "
                        + userFullname
                        + " into group with "
                        + users[0].getUserGroup().getMembersAsString()
                        + ": "
                        + e.getMessage());
              }
            } else {
              Main.logger.warn(
                  "Ignored duplicate secondary user "
                      + userFullname
                      + " who already is in usergroup: "
                      + existingUser.getUserGroup().getMembersAsString());
            }

          } else {
            if (!users[1].getFirstName().equals("")) {
              try {
                users[1].setUserGroup(userGroup);
              } catch (MissingFullMemberException e) {
                Main.logger.warn(e);
                users[1].setNewUserGroup(em);
              }
              users[1].setPassword(
                  hasher.hashToString(
                      4,
                      generateUserRelatedPasswords
                          ? generateUserRelatedToken(users[1].getUsername()).toCharArray()
                          : "start".toCharArray()));
              users[1].setForcePasswordChange(true);
              users[1].getPermissions().add(importPermission);
              em.persist(users[1]);
              importedUsers.put(userFullname, users[1]);
            }
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
    mergeSession.pushAllNotImportantChanges();
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
    Tools.getAll(Supplier.class, null).forEach(e -> suppliers.put(e.getShortName(), e));
    Tools.getAll(PriceList.class, null).forEach(e -> priceListHashMap.put(e.getName(), e));
    suppliers.values().forEach(e -> defaultGroup.put(e, e.getOrPersistDefaultSurchargeGroup(em)));
    ErrorCollector errorCollector = new ErrorCollector();
    f.forEach(
        e -> {
          String[] columns = e.split(";");
          if (!columns[5].equals("SoZ")) {
            try {
              articleCollectionHashMap.put(
                  Articles.parse(
                      columns, barcode, names, suppliers, defaultGroup, priceListHashMap),
                  Articles.extractOffers(columns));
            } catch (CannotParseException ex) {
              errorCollector.collect(ex);
            }
          }
        });
    Main.logger.warn("Ignored " + errorCollector.count() + " articles errors:");
    errorCollector.log();
    ArrayList<Article> articles = new ArrayList<>(articleCollectionHashMap.keySet());
    Tools.group(articles.iterator(), Article::getSupplier)
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

  public void createUserPasswordCsv(@NotNull File selectedFile) {
    if (selectedFile.isDirectory()) throw new IllegalArgumentException("file is a directory");
    try {
      var csvWriter = new CSVWriterBuilder(new FileWriter(selectedFile)).build();
      @Cleanup EntityManager em = DBConnection.getEntityManager();
      @Cleanup(value = "commit")
      EntityTransaction et = em.getTransaction();
      et.begin();
      csvWriter.writeAll(
          em.createQuery("select u from User u", User.class)
              .getResultStream()
              .filter(e -> !e.isKernbeisser() && !e.isSysAdmin())
              .map(
                  e ->
                      new String[] {
                        e.getFirstName(),
                        e.getSurname(),
                        e.getEmail(),
                        e.getUsername(),
                        generateUserRelatedToken(e.getUsername())
                      })
              .collect(Collectors.toCollection(ArrayList::new)));
      csvWriter.flush();

    } catch (IOException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
  }
}
