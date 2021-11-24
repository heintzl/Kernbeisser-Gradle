package kernbeisser.Windows.SynchronizeArticles;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;
import javax.swing.filechooser.FileSystemView;
import kernbeisser.Config.Config;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Key;
import kernbeisser.Tasks.Catalog.Catalog;
import kernbeisser.Tasks.Catalog.Merge.ArticleMerge;
import kernbeisser.Tasks.Catalog.Merge.MappedDifference;
import kernbeisser.Tasks.Catalog.Merge.Solution;
import kernbeisser.Useful.KornKraft;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.Controller;

public class SynchronizeArticleController
    extends Controller<SynchronizeArticleView, SynchronizeArticleModel> {

  @Key(PermissionKey.ACTION_OPEN_SYNCHRONISE_ARTICLE_WINDOW)
  public SynchronizeArticleController() {
    super(new SynchronizeArticleModel());
  }

  @Override
  public void fillView(SynchronizeArticleView synchronizeArticleView) {}

  public void useKernbeisser() {
    applySolution(Solution.KEEP);
  }

  public void useKernbeisserAndIgnore() {
    applySolution(Solution.KEEP_AND_IGNORE);
  }

  public void useKornkraft() {
    applySolution(Solution.UPDATE);
  }

  private void applySolution(Solution solution) {
    new Thread(
            () -> {
              getView().showProgress("Änderungen werden verarbeitet...");
              Collection<ArticleMerge> selection = getView().getSelectedObjects();
              MappedDifference[] differences = getView().getSelectedFilter();
              for (ArticleMerge selectedObject : selection) {
                for (MappedDifference difference : differences) {
                  selectedObject.mergeProperty(difference, solution);
                }
              }
              getView().progressFinished();
              getView().filterTable();
            })
        .start();
  }

  public void setProductGroups() {
    try {
      model.setProductGroups(
          Files.lines(
              getView()
                  .requestInputFile(
                      FileSystemView.getFileSystemView().getHomeDirectory(), "json", "JSON")
                  .toPath()));
    } catch (IOException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
  }

  private void importCatalogSource(Collection<String> lines) {
    model.load(lines);
    getView().setDifferences(model.getAllDiffs());
  }

  public void importCatalogFile() {
    try {
      importCatalogSource(
          Files.readAllLines(
              getView()
                  .requestInputFile(
                      Config.getConfig().getDefaultBnnInboxDir(), "csv", "BNN", "bnn", "txt", "TXT")
                  .toPath(),
              Catalog.DEFAULT_ENCODING));
    } catch (IOException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
  }

  @Override
  protected boolean commitClose() {
    if (model.isCatalogLoaded()) {
      try {
        model.checkDiffs();
      } catch (UnsupportedOperationException e) {
        getView().mergeDiffsFirst();
        return false;
      }
      new Thread(
              () -> {
                getView().showProgress("Datenbank wird auf den neusten Stand gebracht.");
                model.pushToDB();
                getView().progressFinished();
                getView().importSuccessful();
                getView().kill();
              })
          .start();
      return false;
    }
    return true;
  }

  public void importCatalogFromInternet() {
    try {
      String urlAddress = getView().messageRequestInputURL(KornKraft.findLastValidURL().orElse(""));
      URL url = new URL(urlAddress);
      BufferedReader bufferedReader =
          new BufferedReader(new InputStreamReader(url.openStream(), Catalog.DEFAULT_ENCODING));
      importCatalogSource(bufferedReader.lines().collect(Collectors.toCollection(ArrayList::new)));
    } catch (CancellationException ignored) {
    } catch (IOException e) {
      getView().messageInvalidURL();
    }
  }

  public void cancel(ActionEvent actionEvent) {
    if (!getView().commitCancel()) return;
    model.kill();
    getView().back();
  }
}
