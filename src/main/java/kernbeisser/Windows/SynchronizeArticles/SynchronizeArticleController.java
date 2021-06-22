package kernbeisser.Windows.SynchronizeArticles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Key;
import kernbeisser.Tasks.Catalog.Catalog;
import kernbeisser.Tasks.Catalog.Merge.ArticleDifference;
import kernbeisser.Tasks.Catalog.Merge.MappedDifferences;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.Controller;

public class SynchronizeArticleController
    extends Controller<SynchronizeArticleView, SynchronizeArticleModel> {

  @Key(PermissionKey.ACTION_OPEN_SYNCHRONISE_ARTICLE_WINDOW)
  public SynchronizeArticleController() {
    super(new SynchronizeArticleModel());
  }

  @Override
  public void fillView(SynchronizeArticleView synchronizeArticleView) {
    getView().setAllDiffs(MappedDifferences.values());
  }

  public void useKernbeisser() {
    apply(true);
  }

  public void useKernbeisserAndIgnore() {
    new Thread(
            () -> {
              getView().showProgress("Änderungen werden verarbeitet...");
              Collection<ArticleDifference<?>> selection = getView().getSelectedObjects();
              for (ArticleDifference<?> selectedObject : selection) {
                model.resolveAndIgnoreDifference(selectedObject);
              }
              getView().removeAll(selection);
              getView().progressFinished();
            })
        .start();
  }

  public void useKornkraft() {
    apply(false);
  }

  private void apply(boolean useCurrent) {
    new Thread(
            () -> {
              getView().showProgress("Änderungen werden verarbeitet...");
              Collection<ArticleDifference<?>> selection = getView().getSelectedObjects();
              for (ArticleDifference<?> selectedObject : selection) {
                model.resolveDifference(selectedObject, useCurrent);
              }
              getView().removeAll(selection);
              getView().progressFinished();
            })
        .start();
  }

  public void setProductGroups() {
    try {
      model.setProductGroups(Files.lines(getView().requestInputFile("json", "JSON").toPath()));
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
              getView().requestInputFile("csv", "BNN", "bnn", "txt", "TXT").toPath(),
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
      String urlAddress = getView().messageRequestInputURL();
      URL url = new URL(urlAddress);
      BufferedReader bufferedReader =
          new BufferedReader(new InputStreamReader(url.openStream(), Catalog.DEFAULT_ENCODING));
      importCatalogSource(bufferedReader.lines().collect(Collectors.toCollection(ArrayList::new)));
    } catch (CancellationException ignored) {
    } catch (IOException e) {
      getView().messageInvalidURL();
    }
  }
}
