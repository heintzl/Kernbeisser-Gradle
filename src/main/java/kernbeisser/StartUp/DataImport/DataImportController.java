package kernbeisser.StartUp.DataImport;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import kernbeisser.Enums.Setting;
import kernbeisser.Main;
import kernbeisser.Security.PermissionSet;
import kernbeisser.Tasks.Catalog.Catalog;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.SimpleLogIn.SimpleLogInController;
import kernbeisser.Windows.MVC.Controller;
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
              && Files.exists(getPackagePath().resolve(packageDefinition.getKornkraftJson()))
              && packageDefinition.getKornkraftJson() != null
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
          Files.lines(
              getPackagePath().resolve(packageDefinition.getSuppliers()), StandardCharsets.UTF_8);
      Stream<String> article =
          Files.lines(
              getPackagePath().resolve(packageDefinition.getArticles()), StandardCharsets.UTF_8);
      Collection<String> kkCatalog =
          Files.readAllLines(
              getPackagePath().resolve(packageDefinition.getKornkraftCatalog()),
              Catalog.DEFAULT_ENCODING);
      Stream<String> productsJson =
          Files.lines(
              getPackagePath().resolve(packageDefinition.getKornkraftJson()),
              StandardCharsets.UTF_8);
      Stream<String> jobs =
          Files.lines(
              getPackagePath().resolve(packageDefinition.getJobs()), StandardCharsets.US_ASCII);
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
                  model.parseSuppliers(suppliers, view::setItemProgress);
                  model.parsePriceLists(priceLists, view::setItemProgress);
                  model.parseArticle(article, kkCatalog, productsJson, view::setItemProgress);
                  Main.logger.info("Item thread finished");
                });
        articleThread.start();
      }
      if (view.importUser()) {
        new Thread(
                () -> {
                  view.setUserProgress(0);
                  model.parseJobs(jobs, view::setUserProgress);
                  model.parseUsers(user, view::setUserProgress);
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
        String password;
        do {
          password = view.requestPassword();
        } while (password.equals(""));
        model.createAdmin(password);
      }
    } catch (IOException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
    PermissionSet.MASTER.setAllBits(false);
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
}
