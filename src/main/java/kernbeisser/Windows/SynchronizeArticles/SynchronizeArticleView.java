package kernbeisser.Windows.SynchronizeArticles;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.PermissionComboBox;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;

public class SynchronizeArticleView implements View<SynchronizeArticleController> {

    private JPanel main;
    private JButton back;
    private ObjectTable<ArticleDifference<?>> differences;
    private JButton resolveConflicts;
    private PermissionComboBox<String> correctSource;
    private kernbeisser.CustomComponents.TextFields.DoubleParseField maxAllowedDifference;
    private PermissionComboBox<String> diffName;

    @Override
    public void initialize(SynchronizeArticleController controller) {
        back.addActionListener(e -> back());
        differences.setColumns(
                Column.create("Artikel", e -> e.getKernbeisserArticle().getName()),
                Column.create("Unterschied", ArticleDifference::getDifferenceName),
                Column.create("Kernbeisser", ArticleDifference::getKernbeisserVersion, controller::acceptKernbeisser),
                Column.create("Katalog", ArticleDifference::getCatalogVersion, controller::acceptCatalog)
        );
        correctSource.addItem("Katalog");
        correctSource.addItem("Kernbeisser");
        diffName.addItem("Preis");
        diffName.addItem("Gebindegröße");
        diffName.addItem("Einzelpfand");
        diffName.addItem("Kistenpfand");
        diffName.addItem("Packungsmenge");
        resolveConflicts.addActionListener(e -> controller.resolveConflicts());
    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }

    private void createUIComponents() {
        differences = new ObjectTable<>();
    }

    void setDifferences(Collection<ArticleDifference<?>> articleDifference) {
        differences.setObjects(articleDifference);
    }

    void remove(ArticleDifference<?> articleDifference) {
        differences.remove(articleDifference);
    }

    public double getAllowedDifference() {
        return maxAllowedDifference.getSafeValue();
    }

    public String getDiffName() {
        return (String) diffName.getSelectedItem();
    }

    public String getSource() {
        return (String) correctSource.getSelectedItem();
    }

    public void setResolveConflictsEnabled(boolean b) {
        resolveConflicts.setEnabled(b);
    }
}
