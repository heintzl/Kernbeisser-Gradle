package kernbeisser.Windows.SynchronizeArticles;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;

public class SynchronizeArticleView implements View<SynchronizeArticleController> {

    private JPanel main;
    private JButton back;
    private ObjectTable<ArticleDifference<?>> differences;

    @Override
    public void initialize(SynchronizeArticleController controller) {
        back.addActionListener(e -> back());
        differences.setColumns(
                Column.create("Artikel",e -> e.getKernbeisserArticle().getName()),
                Column.create("Unterschied", ArticleDifference::getDifferenceName),
                Column.create("Kernbeisser", ArticleDifference::getKernbeisserVersion, controller::acceptKernbeisser),
                Column.create("Katalog", ArticleDifference::getCatalogVersion, controller::acceptCatalog)
                );
    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }

    private void createUIComponents() {
        differences = new ObjectTable<>();
    }

    void setDifferences(Collection<ArticleDifference<?>> articleDifference){
        differences.setObjects(articleDifference);
    }

    void remove(ArticleDifference<?> articleDifference){
        differences.remove(articleDifference);
    }
}
