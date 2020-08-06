package kernbeisser.Windows.SynchronizeArticles;

import javax.swing.*;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Main;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.AutoInitialize;
import kernbeisser.Windows.Controller;
import org.jetbrains.annotations.NotNull;

@AutoInitialize
public class SynchronizeArticleController
    implements Controller<SynchronizeArticleView, SynchronizeArticleModel> {

  private final SynchronizeArticleModel model;
  @AutoInitialize private SynchronizeArticleView view;

  public SynchronizeArticleController() {
    model = new SynchronizeArticleModel();
  }

  @NotNull
  @Override
  public SynchronizeArticleView getView() {
    return view;
  }

  @NotNull
  @Override
  public SynchronizeArticleModel getModel() {
    return model;
  }

  @Override
  public void fillUI() {
    view.setDifferences(model.getAllDifferences());
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }

  public void acceptKernbeisser(ArticleDifference<?> articleDifference) {
    articleDifference.applyKernbeisser();
    model.getAllDifferences().remove(articleDifference);
    view.remove(articleDifference);
  }

  public void acceptCatalog(ArticleDifference<?> articleDifference) {
    articleDifference.applyCatalog();
    model.getAllDifferences().remove(articleDifference);
    view.remove(articleDifference);
  }

  void resolveConflicts() {
    new Thread(
            () -> {
              view.setResolveConflictsEnabled(false);
              double allowedDifference = view.getAllowedDifference() / 100;
              String name = view.getDiffName();
              String source = view.getSource();
              model
                  .getAllDifferences()
                  .removeIf(
                      e -> {
                        if (e.getDifferenceName().equals(name)) {
                          try {
                            ArticleDifference<Number> numberDifference =
                                (ArticleDifference<Number>) e;
                            if (Math.abs(
                                    numberDifference.getKernbeisserVersion().doubleValue()
                                        - numberDifference.getCatalogVersion().doubleValue())
                                < numberDifference.getKernbeisserVersion().doubleValue()
                                    * allowedDifference) {
                              switch (source) {
                                case "Kernbeisser":
                                  numberDifference.applyKernbeisser();
                                  return true;
                                case "Katalog":
                                  numberDifference.applyCatalog();
                                  return true;
                                default:
                                  return false;
                              }
                            } else {
                              return false;
                            }
                          } catch (ClassCastException exception) {
                            Tools.showUnexpectedErrorWarning(exception);
                            return false;
                          }
                        } else {
                          return false;
                        }
                      });
              fillUI();
              view.setResolveConflictsEnabled(true);
            })
        .start();
  }

  public static void main(String[] args) throws UnsupportedLookAndFeelException {
    Main.buildEnvironment();
    Main.checkCatalog();

    new SynchronizeArticleController().openTab("Hello");
  }
}
