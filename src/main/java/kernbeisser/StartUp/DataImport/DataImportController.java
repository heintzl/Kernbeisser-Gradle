package kernbeisser.StartUp.DataImport;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.PermissionConstants;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.TransactionType;
import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Main;
import kernbeisser.Security.PermissionSet;
import kernbeisser.Tasks.Articles;
import kernbeisser.Tasks.Users;
import kernbeisser.Useful.ErrorCollector;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.SimpleLogIn.SimpleLogInController;
import kernbeisser.Windows.MVC.Controller;
import lombok.Cleanup;
import lombok.var;
import org.jetbrains.annotations.NotNull;

public class DataImportController extends Controller<DataImportView, DataImportModel> {

  public DataImportController() {
    super(new DataImportModel());
  }

  void openFileExplorer() {
    var view = getView();
    File file = new File("importPath.txt");
    String importPath = ".";
    if (file.exists()) {
      try {
        List<String> fileLines = Files.readAllLines(file.toPath());
        importPath = fileLines.get(0);
      } catch (IOException e) {
        Tools.showUnexpectedErrorWarning(e);
      }
    }
    JFileChooser jFileChooser = new JFileChooser(importPath);
    jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    jFileChooser.setFileFilter(new FileNameExtensionFilter("Config-File", "JSON", "json"));
    jFileChooser.addActionListener(
        e -> {
          if (jFileChooser.getSelectedFile() == null) {
            return;
          }
          view.setFilePath(jFileChooser.getSelectedFile().getAbsolutePath());
          checkDataSource();
        });
    jFileChooser.showOpenDialog(view.getTopComponent());
  }

  private PackageDefinition readFromPath(Path path) throws IOException {
    try {
      PackageDefinition packageDefinition =
          new Gson()
              .fromJson(Files.lines(path).collect(Collectors.joining()), PackageDefinition.class);
      if (!packageDefinition.getType().equals(PackageDefinition.TYPE_MARK)) {
        throw new UnsupportedOperationException("file contains wrong input");
      }
      return packageDefinition;
    } catch (JsonSyntaxException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  private PackageDefinition extractPackageDefinition() throws IOException {
    return readFromPath(Paths.get(getView().getFilePath()));
  }

  private Path getPackagePath() {
    return Paths.get(getView().getFilePath()).getParent();
  }

  void checkDataSource() {
    var view = getView();
    try {
      PackageDefinition packageDefinition = extractPackageDefinition();
      view.articleSourceFound(
          packageDefinition.getArticles() != null
              && Files.exists(getPackagePath().resolve(packageDefinition.getArticles()))
              && packageDefinition.getSuppliers() != null
              && Files.exists(getPackagePath().resolve(packageDefinition.getSuppliers()))
              && packageDefinition.getPriceLists() != null
              && Files.exists(getPackagePath().resolve(packageDefinition.getPriceLists())));
      view.userSourceFound(
          packageDefinition.getUser() != null
              && Files.exists(getPackagePath().resolve(packageDefinition.getUser()))
              && packageDefinition.getJobs() != null
              && Files.exists(getPackagePath().resolve(packageDefinition.getJobs())));

    } catch (IOException e) {
      view.setValidDataSource(false);
    }
  }

  Thread articleThread = null;

  void importData() {
    PermissionSet.MASTER.setAllBits(true);
    try {
      PackageDefinition packageDefinition = extractPackageDefinition();
      Stream<String> suppliers =
          Files.lines(getPackagePath().resolve(packageDefinition.getSuppliers()));
      Stream<String> article =
          Files.lines(getPackagePath().resolve(packageDefinition.getArticles()));
      Stream<String> jobs = Files.lines(getPackagePath().resolve(packageDefinition.getJobs()));
      Stream<String> priceLists =
          Files.lines(getPackagePath().resolve(packageDefinition.getPriceLists()));
      Stream<String> user = Files.lines(getPackagePath().resolve(packageDefinition.getUser()));
      var view = getView();
      Main.logger.info("Starting importing data");
      Setting.DB_INITIALIZED.changeValue(true);
      if (view.importItems()) {
        articleThread =
            new Thread(
                () -> {
                  view.setItemProgress(0);
                  parseSuppliers(suppliers);
                  parsePriceLists(priceLists);
                  parseArticle(article);
                  Main.logger.info("Item thread finished");
                });
        articleThread.start();
      }
      if (view.importUser()) {
        new Thread(
                () -> {
                  view.setUserProgress(0);
                  parseJobs(jobs);
                  parseUsers(user);
                  Main.logger.info("User thread finished");
                  try {
                    articleThread.join();
                  } catch (InterruptedException e) {
                    Tools.showUnexpectedErrorWarning(e);
                  }
                  view.back();
                })
            .start();
      } else {
        view.userSourceFound(false);
        view.userSourcesNotExists();
      }
      if (view.createStandardAdmin()) {
        createAdmin();
      }
    } catch (IOException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
    PermissionSet.MASTER.setAllBits(false);
  }

  private void createAdmin() {
    User user = new User();
    user.setFirstName("System");
    user.setSurname("Admin");
    user.setUsername("Admin");
    String password;
    var view = getView();
    do {
      password = view.requestPassword();
    } while (password.equals(""));
    user.setPassword(
        BCrypt.withDefaults()
            .hashToString(Setting.HASH_COSTS.getIntValue(), password.toCharArray()));
    user.getPermissions().add(PermissionConstants.ADMIN.getPermission());
    user.setUserGroup(new UserGroup());
    Tools.persist(user.getUserGroup());
    Tools.persist(user);
  }

  private void parseJobs(Stream<String> f) {
    Collection<Job> jobs = new ArrayList<>(20);
    f.forEach(
        e -> {
          String[] columns = e.split(";");
          Job job = new Job();
          job.setName(columns[0]);
          job.setDescription(columns[1]);
          jobs.add(job);
        });
    var view = getView();
    view.setUserProgress(1);
    model.batchMergeAll(jobs);
    view.setUserProgress(2);
  }

  private void parseUsers(Stream<String> f) {
    var view = getView();
    HashSet<String> usernames = new HashSet<>();
    HashMap<String, Job> jobs = new HashMap<>();
    Job.getAll(null).forEach(e -> jobs.put(e.getName(), e));
    Permission importPermission = PermissionConstants.IMPORT.getPermission();
    Permission keyPermission = PermissionConstants.KEY_PERMISSION.getPermission();
    User kernbeisser = User.getKernbeisserUser();
    BCrypt.Hasher hasher = BCrypt.withDefaults();
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    f.forEach(
        l -> {
          String[] rawUserData = l.split(";");

          User[] users = Users.parse(rawUserData, usernames, jobs);

          UserGroup userGroup = Users.getUserGroup(rawUserData);

          users[0].setUserGroup(userGroup);
          users[1].setUserGroup(userGroup);

          String defaultPassword = hasher.hashToString(4, "start".toCharArray());
          users[0].setPassword(defaultPassword);
          users[1].setPassword(defaultPassword);

          userGroup.setValue(Users.getValue(rawUserData));
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

          Transaction transaction = new Transaction();
          transaction.setTransactionType(TransactionType.INITIALIZE);
          transaction.setFrom(kernbeisser);
          transaction.setValue(Users.getValue(rawUserData));
          transaction.setInfo("Übertrag des Guthaben des alten Kernbeisser Programmes");
          transaction.setTo(users[0]);
          em.persist(transaction);
        });
    em.flush();
    et.commit();
    em.close();
    view.setUserProgress(4);
  }

  private void parsePriceLists(Stream<String> f) {
    var view = getView();
    HashMap<String, PriceList> priceLists = new HashMap<>();
    f.forEach(
        e -> {
          String[] columns = e.split(";");
          PriceList pl = new PriceList();
          pl.setName(columns[0]);
          pl.setSuperPriceList(priceLists.get(columns[1]));
          priceLists.put(pl.getName(), pl);
        });
    view.setItemProgress(3);
    model.saveAll(priceLists.values());
    view.setItemProgress(4);
  }

  private void parseSuppliers(Stream<String> f) {
    Collection<Supplier> suppliers = new ArrayList<>();
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
          suppliers.add(supplier);
        });
    var view = getView();
    view.setItemProgress(1);
    model.batchMergeAll(suppliers);
    view.setItemProgress(2);
  }

  private void parseArticle(Stream<String> f) {
    HashSet<Long> barcode = new HashSet<>(5000);
    HashSet<String> names = new HashSet<>();
    HashMap<String, PriceList> priceListHashMap = new HashMap<>();
    HashMap<String, Supplier> suppliers = new HashMap<>();
    HashMap<Article, Collection<Offer>> articleCollectionHashMap = new HashMap<>(5000);
    Tools.getAllUnProxy(Supplier.class).forEach(e -> suppliers.put(e.getShortName(), e));
    Tools.getAllUnProxy(PriceList.class).forEach(e -> priceListHashMap.put(e.getName(), e));
    ErrorCollector errorCollector = new ErrorCollector();
    f.forEach(
        e -> {
          String[] columns = e.split(";");
          try {
            articleCollectionHashMap.put(
                Articles.parse(columns, barcode, names, suppliers, priceListHashMap),
                Articles.extractOffers(columns));
          } catch (CannotParseException ex) {
            errorCollector.collect(ex);
          }
        });
    Main.logger.warn("Ignored " + errorCollector.count() + " articles errors:");
    errorCollector.log();
    var view = getView();
    view.setItemProgress(5);
    model.saveAllItems(articleCollectionHashMap);
    view.setItemProgress(6);
  }

  void cancel() {
    var view = getView();
    view.back();
    Setting.DB_INITIALIZED.changeValue(true);
  }

  @Override
  public boolean commitClose() {
    new SimpleLogInController().openTab();
    return true;
  }

  @Override
  public @NotNull DataImportModel getModel() {
    return model;
  }

  @Override
  public void fillView(DataImportView dataImportView) {}

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }
}
