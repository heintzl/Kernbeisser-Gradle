package kernbeisser.Windows.SynchronizeArticles;

import javax.swing.*;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Main;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IController;
import org.jetbrains.annotations.NotNull;

public class SynchronizeArticleController
    implements IController<SynchronizeArticleView, SynchronizeArticleModel> {

  private final SynchronizeArticleModel model;
  private SynchronizeArticleView view;

  public SynchronizeArticleController() {
    model = new SynchronizeArticleModel();
  }

  @NotNull
  @Override
  public SynchronizeArticleModel getModel() {
    return model;
  }

  @Override
  public void fillUI() {
    view.setDifferences(model.getAllDifferences());
    view.setAllDiffsTypes(DifferenceType.values());
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

    new SynchronizeArticleController().openTab("Hello");
  }

  public void filter() {
    view.setObjectFilter();
  }

  void useKernbeisser() {
    model.resolve(view.getSelectedObjects(), true);
    view.setDifferences(model.getAllDifferences());
  }

  void useKornkraft() {
    model.resolve(view.getSelectedObjects(), false);
    view.setDifferences(model.getAllDifferences());
  }

  @Override
  public boolean commitClose() {
    return model.getAllDifferences().size() == 0 || view.commitClose();
  }
}
