package kernbeisser.Windows.SynchronizeArticles;

import java.io.IOException;
import java.nio.file.Files;
import javax.swing.*;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Tasks.Catalog.Catalog;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.Controller;
import org.jetbrains.annotations.NotNull;

public class SynchronizeArticleController
    extends Controller<SynchronizeArticleView, SynchronizeArticleModel> {

  public SynchronizeArticleController() {
    super(new SynchronizeArticleModel());
  }

  @NotNull
  @Override
  public SynchronizeArticleModel getModel() {
    return model;
  }

  @Override
  public void fillView(SynchronizeArticleView synchronizeArticleView) {
    synchronizeArticleView.setDifferences(model.getAllDifferences());
    synchronizeArticleView.setAllDiffsTypes(DifferenceType.values());
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }

  public double getDifference(ArticleDifference<?> difference) {
    try {
      ArticleDifference<Number> numberDifference = (ArticleDifference<Number>) difference;
      return Math.abs(
              numberDifference.getCatalogVersion().doubleValue()
                  / numberDifference.getKernbeisserVersion().doubleValue())
          - 1;
    } catch (ClassCastException e) {
      Tools.showUnexpectedErrorWarning(e);
      throw new RuntimeException(e);
    }
  }

  void importCatalog() {
    try {
      getView().setImportCatalogAvailable(false);
      Catalog.updateCatalog(
          Files.lines(
              getView().requestInputFile("csv", "bnn", "BNN", "txt").toPath(),
              Catalog.DEFAULT_ENCODING),
          () -> {
            getView().setImportCatalogAvailable(true);
            getView().importSuccessful();
            model.refreshDiffs();
            getView().setDifferences(model.getAllDifferences());
          });
      getView().progressStarted();
    } catch (IOException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
  }

  public void filter() {
    getView().setObjectFilter();
  }

  void useKernbeisser() {
    model.resolve(getView().getSelectedObjects(), true);
    getView().setDifferences(model.getAllDifferences());
  }

  void useKornkraft() {
    model.resolve(getView().getSelectedObjects(), false);
    getView().setDifferences(model.getAllDifferences());
  }

  void linkSurchargeGroups() {
    try {
      model.setProductGroups(
          Files.lines(
              getView().requestInputFile("csv", "bnn", "BNN", "txt").toPath(),
              Catalog.DEFAULT_ENCODING));
      getView().surchargeGroupsSet();
    } catch (IOException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
  }

  @Override
  public boolean commitClose() {
    return model.getAllDifferences().size() == 0 || getView().commitClose();
  }
}
