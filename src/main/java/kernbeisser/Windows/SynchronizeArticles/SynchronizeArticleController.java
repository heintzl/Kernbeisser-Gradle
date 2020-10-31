package kernbeisser.Windows.SynchronizeArticles;

import javax.swing.*;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Main;
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

  public static void main(String[] args) throws UnsupportedLookAndFeelException {
    Main.buildEnvironment();
    Main.checkCatalog();

    new SynchronizeArticleController().openTab();
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

  @Override
  public boolean commitClose() {
    return model.getAllDifferences().size() == 0 || getView().commitClose();
  }
}
