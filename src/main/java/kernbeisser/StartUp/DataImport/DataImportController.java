package kernbeisser.StartUp.DataImport;

import at.favre.lib.crypto.bcrypt.BCrypt;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.PermissionConstants;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.TransactionType;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Main;
import kernbeisser.Security.MasterPermissionSet;
import kernbeisser.Tasks.Articles;
import kernbeisser.Tasks.Users;
import kernbeisser.Useful.ErrorCollector;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.SimpleLogIn.SimpleLogInController;
import kernbeisser.Windows.MVC.Controller;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class DataImportController implements Controller<DataImportView, DataImportModel> {
  private DataImportView view;
  private final DataImportModel model;

  public DataImportController() {
    model = new DataImportModel();
  }

  void openFileExplorer() {
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
    jFileChooser.showOpenDialog(getView().getTopComponent());
  }

  private boolean isValidDataSource() {
    return view.getFilePath().toUpperCase().endsWith(".JSON")
        && new File(view.getFilePath()).exists();
  }

  void checkDataSource() {
    if (isValidDataSource()) {
      view.setValidDataSource(true);
      JSONObject dataConfig = extractJSON();
      if (dataConfig.has("UserData")) {
        JSONObject jsonObject = dataConfig.getJSONObject("UserData");
        view.userSourceFound(jsonObject.has("Users") && jsonObject.has("Jobs"));
      } else {
        view.userSourceFound(false);
      }
      if (dataConfig.has("ItemData")) {
        JSONObject jsonObject = dataConfig.getJSONObject("ItemData");
        view.itemSourceFound(
            jsonObject.has("Suppliers") && jsonObject.has("Items") && jsonObject.has("PriceLists"));
      } else {
        view.itemSourceFound(false);
      }
    } else {
      view.setValidDataSource(false);
      view.userSourceFound(false);
      view.itemSourceFound(false);
    }
  }

  private JSONObject extractJSON() {
    StringBuilder sb = new StringBuilder();
    try {
      Files.readAllLines(new File(view.getFilePath()).toPath()).forEach(sb::append);
    } catch (IOException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
    return new JSONObject(sb.toString());
  }

  void importData() {
    MasterPermissionSet.setAllBits(true);
    if (isValidDataSource()) {
      Main.logger.info("Starting importing data");
      File jsonPath = new File(view.getFilePath()).getParentFile();
      JSONObject path = extractJSON();
      Setting.DB_INITIALIZED.setValue(true);
      if (view.importItems()) {
        JSONObject itemPath = path.getJSONObject("ItemData");
        File suppliers = new File(jsonPath, itemPath.getString("Suppliers"));
        File priceLists = new File(jsonPath, itemPath.getString("PriceLists"));
        File items = new File(jsonPath, itemPath.getString("Items"));
        if (suppliers.exists() && priceLists.exists() && items.exists()) {
          new Thread(
                  () -> {
                    view.setItemProgress(0);
                    parseSuppliers(suppliers);
                    parsePriceLists(priceLists);
                    parseItems(items);
                    Main.logger.info("Item thread finished");
                    view.back();
                  })
              .start();
        } else {
          view.itemSourceFound(false);
          view.itemSourcesNotExists();
        }
      }
      if (view.importUser()) {
        JSONObject userPath = path.getJSONObject("UserData");
        File users = new File(jsonPath, userPath.getString("Users"));
        File jobs = new File(jsonPath, userPath.getString("Jobs"));
        if (jobs.exists() && users.exists()) {
          new Thread(
                  () -> {
                    view.setUserProgress(0);
                    parseJobs(jobs);
                    parseUsers(users);
                    Main.logger.info("User thread finished");
                  })
              .start();
        } else {
          view.userSourceFound(false);
          view.userSourcesNotExists();
        }
      }
      if (view.createStandardAdmin()) {
        createAdmin();
      }
    }
    MasterPermissionSet.setAllBits(false);
  }

  private void createAdmin() {
    User user = new User();
    user.setFirstName("System");
    user.setSurname("Admin");
    user.setUsername("Admin");
    String password;
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

  private void parseJobs(File f) {
    try {
      List<String> lines = Files.readAllLines(f.toPath(), StandardCharsets.UTF_8);
      Collection<Job> jobs = new ArrayList<>((int) (lines.size() * 1.5));
      for (String line : lines) {
        String[] columns = line.split(";");
        Job job = new Job();
        job.setName(columns[0]);
        job.setDescription(columns[1]);
        jobs.add(job);
      }
      view.setUserProgress(1);
      model.batchMergeAll(jobs);
      view.setUserProgress(2);
    } catch (IOException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
  }

  private void parseUsers(File f) {
    try {
      HashSet<String> usernames = new HashSet<>();
      HashMap<String, Job> jobs = new HashMap<>();
      Job.getAll(null).forEach(e -> jobs.put(e.getName(), e));
      List<String> lines = Files.readAllLines(f.toPath(), StandardCharsets.UTF_8);
      for (String l : lines) {
        String[] rawUserData = l.split(";");

        User[] users = Users.parse(rawUserData, usernames, jobs);

        UserGroup userGroup = Users.getUserGroup(rawUserData);

        users[0].setUserGroup(userGroup);
        users[1].setUserGroup(userGroup);

        String defaultPassword =
            BCrypt.withDefaults()
                .hashToString(Setting.HASH_COSTS.getIntValue(), "start".toCharArray());
        users[0].setPassword(defaultPassword);
        users[1].setPassword(defaultPassword);

        Tools.persist(userGroup);

        users[0].getPermissions().add(PermissionConstants.IMPORT.getPermission());
        users[1].getPermissions().add(PermissionConstants.IMPORT.getPermission());

        if (users[0].getKernbeisserKey() != -1) {
          users[0].getPermissions().add(PermissionConstants.KEY_PERMISSION.getPermission());
        }

        Tools.persist(users[0]);

        if (!users[1].getFirstName().equals("")) {
          Tools.persist(users[1]);
        }

        MasterPermissionSet.addPermission(PermissionKey.GO_UNDER_MIN);

        Transaction.doTransaction(
            User.getKernbeisserUser(),
            users[0],
            Users.getValue(rawUserData),
            TransactionType.INITIALIZE,
            "Übertrag des Guthaben des alten Kernbeisser Programmes");

        MasterPermissionSet.removePermission(PermissionKey.GO_UNDER_MIN);
      }
      view.setUserProgress(4);
    } catch (IOException | AccessDeniedException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
  }

  private void generateUsername(HashSet<String> usernames, User user) {
    for (int i = 1; i < user.getSurname().length(); i++) {
      String generatedUsername =
          (user.getFirstName().split(" ")[0] + "." + user.getSurname().substring(0, i))
              .toLowerCase();
      if (!usernames.contains(generatedUsername)) {
        user.setUsername(generatedUsername);
        usernames.add(generatedUsername);
        break;
      }
    }
    if (user.getUsername() == null) {
      user.setUsername(user.getFirstName() + "." + user.getSurname() + new Random().nextLong());
    }
  }

  private void parsePriceLists(File f) {
    try {
      List<String> lines = Files.readAllLines(f.toPath(), StandardCharsets.UTF_8);
      HashMap<String, PriceList> priceLists = new HashMap<>();
      for (String l : lines) {
        String[] columns = l.split(";");
        PriceList pl = new PriceList();
        pl.setName(columns[0]);
        pl.setSuperPriceList(priceLists.get(columns[1]));
        priceLists.put(pl.getName(), pl);
      }
      view.setItemProgress(3);
      model.saveAll(priceLists.values());
      view.setItemProgress(4);
    } catch (IOException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
  }

  private void parseSuppliers(File f) {
    try {
      List<String> lines = Files.readAllLines(f.toPath(), StandardCharsets.UTF_8);
      Collection<Supplier> suppliers = new ArrayList<>(lines.size());
      for (String l : lines) {
        String[] columns = l.split(";");
        Supplier supplier = new Supplier();
        supplier.setShortName(columns[0]);
        supplier.setName(columns[1]);
        supplier.setPhoneNumber(columns[2]);
        supplier.setEmail(columns[3]);
        supplier.setFax(columns[4]);
        supplier.setStreet(columns[5]);
        supplier.setLocation(columns[6]);
        supplier.setKeeper(columns[7]);
        supplier.setSurcharge(Integer.parseInt(columns[8]));
        suppliers.add(supplier);
      }
      view.setItemProgress(1);
      model.batchMergeAll(suppliers);
      view.setItemProgress(2);
    } catch (IOException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
  }

  private void parseItems(File f) {
    try {
      List<String> lines = Files.readAllLines(f.toPath(), StandardCharsets.UTF_8);
      HashSet<Long> barcode = new HashSet<>(lines.size());
      HashSet<String> names = new HashSet<>();
      HashMap<String, PriceList> priceListHashMap = new HashMap<>();
      HashMap<String, Supplier> suppliers = new HashMap<>();
      Collection<Article> articles = new ArrayList<>(lines.size());
      Supplier.getAll(null).forEach(e -> suppliers.put(e.getShortName(), e));
      PriceList.getAll(null).forEach(e -> priceListHashMap.put(e.getName(), e));
      ErrorCollector errorCollector = new ErrorCollector();
      for (String l : lines) {
        String[] columns = l.split(";");
        try {
          articles.add(Articles.parse(columns, barcode, names, suppliers, priceListHashMap));
        } catch (CannotParseException e) {
          errorCollector.collect(e);
        }
      }
      Main.logger.warn("Ignored " + errorCollector.count() + " articles errors:");
      errorCollector.log();
      view.setItemProgress(5);
      model.saveAllItems(articles);
      view.setItemProgress(6);
    } catch (IOException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
  }

  void cancel() {
    view.back();
    Setting.DB_INITIALIZED.setValue(true);
  }

  @Override
  public boolean commitClose() {
    new SimpleLogInController().openTab("Log In");
    return true;
  }

  @Override
  public @NotNull DataImportModel getModel() {
    return model;
  }

  @Override
  public void fillUI() {}

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }
}
