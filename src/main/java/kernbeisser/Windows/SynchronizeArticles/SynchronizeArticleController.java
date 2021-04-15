package kernbeisser.Windows.SynchronizeArticles;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
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

  public void importCatalog() {
    try {
      model.load(
          Files.readAllLines(
              getView().requestInputFile("csv", "BNN", "bnn", "txt", "TXT").toPath(),
              Catalog.DEFAULT_ENCODING));

      getView().setDifferences(model.getAllDiffs());
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
}
