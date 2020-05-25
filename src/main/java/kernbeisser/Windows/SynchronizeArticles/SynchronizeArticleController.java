package kernbeisser.Windows.SynchronizeArticles;

import kernbeisser.Enums.Key;
import kernbeisser.Main;
import kernbeisser.Windows.Controller;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class SynchronizeArticleController implements Controller<SynchronizeArticleView,SynchronizeArticleModel> {

    private final SynchronizeArticleModel model;
    private final SynchronizeArticleView view;

    public SynchronizeArticleController() {
        model = new SynchronizeArticleModel();
        view = new SynchronizeArticleView();
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
    public Key[] getRequiredKeys() {
        return new Key[0];
    }

    public void acceptKernbeisser(ArticleDifference<?> articleDifference) {
        articleDifference.applyKernbeisser();
        view.remove(articleDifference);
    }

    public void acceptCatalog(ArticleDifference<?> articleDifference) {
        articleDifference.applyCatalog();
        view.remove(articleDifference);
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        Main.buildEnvironment();
        Main.checkCatalog();

        new SynchronizeArticleController().openTab("Hello");
    }
}
