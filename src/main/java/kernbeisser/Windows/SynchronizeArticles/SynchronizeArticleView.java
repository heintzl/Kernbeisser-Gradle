package kernbeisser.Windows.SynchronizeArticles;

import java.util.Collection;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.ObjectTable.RowFilter;
import kernbeisser.CustomComponents.PermissionComboBox;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class SynchronizeArticleView implements IView<SynchronizeArticleController> {

  private JPanel main;
  private JButton back;
  private ObjectTable<ArticleDifference<?>> differences;
  private JButton filter;
  private kernbeisser.CustomComponents.TextFields.DoubleParseField maxAllowedDifference;
  private PermissionComboBox<DifferenceType> diffName;
  private JButton useKernbeisser;
  private JButton useKornkraft;
  private JButton selectAll;
  private JButton removeSelection;

  @Linked private SynchronizeArticleController controller;

  @Override
  public void initialize(SynchronizeArticleController controller) {
    back.addActionListener(e -> back());
    differences.setColumns(
        Column.create("Artikel", e -> e.getKernbeisserArticle().getName()),
        Column.create("Kornkraftnummer", e -> e.getKernbeisserArticle().getSuppliersItemNumber()),
        Column.create(
            "Abweichung", e -> String.format("%.2f%%", controller.getDifference(e) * 100)),
        Column.create("Unterschied", ArticleDifference::getDifferenceType),
        Column.create("Kernbeisser", ArticleDifference::getKernbeisserVersion),
        Column.create("Katalog", ArticleDifference::getCatalogVersion));
    filter.addActionListener(e -> controller.filter());
    selectAll.addActionListener(
        e -> {
          differences.requestFocusInWindow();
          differences.selectAll();
        });
    diffName.addActionListener(e -> setObjectFilter());
    maxAllowedDifference.addActionListener(e -> setObjectFilter());
    useKernbeisser.addActionListener(e -> controller.useKernbeisser());
    useKornkraft.addActionListener(e -> controller.useKornkraft());
    removeSelection.addActionListener(e -> differences.clearSelection());
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
    if (articleDifference.size() > 0) differences.getSelectionModel().setSelectionInterval(0, 0);
  }

  void remove(ArticleDifference<?> articleDifference) {
    differences.remove(articleDifference);
  }

  public double getAllowedDifference() {
    return maxAllowedDifference.getSafeValue() / 100;
  }

  public DifferenceType getDiffName() {
    return (DifferenceType) diffName.getSelectedItem();
  }

  public void setFilterAvailable(boolean b) {
    filter.setEnabled(b);
  }

  public void setObjectFilter() {
    differences.setRowFilter(
        new RowFilter<ArticleDifference<?>>() {
          private final double allowedDiff = getAllowedDifference();
          private final boolean filterDiff = !maxAllowedDifference.getText().equals("");
          private final DifferenceType selectedDiffType = getDiffName();

          @Override
          public boolean isDisplayed(ArticleDifference<?> difference) {
            if (filterDiff
                && Math.abs(SynchronizeArticleView.this.controller.getDifference(difference))
                    > allowedDiff) {
              return false;
            }
            if (!difference.getDifferenceType().equals(selectedDiffType)) return false;
            return true;
          }
        });
  }

  void setAllDiffsTypes(DifferenceType[] diffsTypes) {
    diffName.removeAllItems();
    ;
    for (DifferenceType diffsType : diffsTypes) {
      diffName.addItem(diffsType);
    }
  }

  Collection<ArticleDifference<?>> getSelectedObjects() {
    return differences.getSelectedObjects();
  }

  public boolean commitClose() {
    differences.setRowFilter(null);
    return JOptionPane.showConfirmDialog(
            getTopComponent(),
            "Es sind noch unsynchonisierte Artikel vorhanden,\nwollen sie das Fester wirklich schließen?")
        == 0;
  }
}
