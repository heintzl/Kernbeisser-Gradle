package kernbeisser.Windows.SynchronizeArticles;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.ObjectTable.RowFilter;
import kernbeisser.CustomComponents.TextFields.DoubleParseField;
import kernbeisser.DBEntities.Article;
import kernbeisser.Tasks.Catalog.Merge.ArticleDifference;
import kernbeisser.Tasks.Catalog.Merge.Difference;
import kernbeisser.Tasks.Catalog.Merge.MappedDifferences;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class SynchronizeArticleView implements IView<SynchronizeArticleController> {

  private JPanel main;
  private JButton back;
  private ObjectTable<ArticleDifference<?>> differences;
  private JButton filter;
  private JButton useKernbeisser;
  private JButton useKornkraft;
  private JButton selectAll;
  private JButton removeSelection;
  private JButton importCatalog;
  private JButton autoLinkCatalogSurchargeGroups;
  private AdvancedComboBox<MappedDifferences> difference;
  private DoubleParseField maxAllowedDiff;
  private JButton useKernbeisserAndIgnore;

  @Linked private SynchronizeArticleController controller;

  @Override
  public void initialize(SynchronizeArticleController controller) {
    back.addActionListener(e -> back());
    differences.setColumns(
        Column.create("Artikel", e -> e.getArticle().getName()),
        Column.create("Kornkraftnummer", e -> e.getArticle().getSuppliersItemNumber()),
        Column.create("Unterschied", e -> e.getArticleDifference().getName()),
        Column.create("Abweichung", e -> String.format("%.2f%%", e.distance() * 100)),
        Column.create("Kernbeisser", ArticleDifference::getPreviousVersion),
        Column.create("Katalog", ArticleDifference::getNewVersion));
    selectAll.addActionListener(
        e -> {
          differences.requestFocusInWindow();
          differences.selectAll();
        });
    maxAllowedDiff.addActionListener(e -> setObjectFilter());
    filter.addActionListener(e -> setObjectFilter());
    useKernbeisser.addActionListener(e -> controller.useKernbeisser());
    useKornkraft.addActionListener(e -> controller.useKornkraft());
    removeSelection.addActionListener(e -> differences.clearSelection());
    useKernbeisserAndIgnore.addActionListener(e -> controller.useKernbeisserAndIgnore());
    importCatalog.addActionListener(
        e -> {
          controller.importCatalog();
        });
    autoLinkCatalogSurchargeGroups.addActionListener(e -> controller.setProductGroups());
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private void createUIComponents() {
    differences = new ObjectTable<>();
    maxAllowedDiff = new DoubleParseField();
    difference = new AdvancedComboBox<>(Difference::getName);
  }

  void setDifferences(Collection<ArticleDifference<?>> articleDifference) {
    differences.setObjects(articleDifference);
    if (articleDifference.size() > 0) differences.getSelectionModel().setSelectionInterval(0, 0);
  }

  void remove(ArticleDifference<?> articleDifference) {
    differences.remove(articleDifference);
  }

  public double getAllowedDifference() {
    return maxAllowedDiff.getSafeValue() / 100;
  }

  public void setFilterAvailable(boolean b) {
    filter.setEnabled(b);
  }

  public void setObjectFilter() {
    differences.setRowFilter(
        new RowFilter<ArticleDifference<?>>() {
          private final double allowedDiff = getAllowedDifference();
          private final boolean filterDiff = !maxAllowedDiff.getText().equals("");
          private final MappedDifferences type = difference.getSelected();

          @Override
          public boolean isDisplayed(ArticleDifference<?> difference) {
            return type.equals(difference.getArticleDifference())
                && (!filterDiff || allowedDiff > Math.abs(difference.distance()));
          }
        });
  }

  Collection<ArticleDifference<?>> getSelectedObjects() {
    return differences.getSelectedObjects();
  }

  public boolean commitClose() {
    differences.setRowFilter(null);
    return JOptionPane.showConfirmDialog(
            getTopComponent(),
            "Es sind noch unsynchronisierte Artikel vorhanden,\nwillst du das Fenster wirklich schließen?")
        == 0;
  }

  @Override
  public String getTitle() {
    return "Katalog sychronisieren";
  }

  public File requestInputFile(String... extensions) {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileFilter(new FileNameExtensionFilter("Source-File", extensions));
    fileChooser.showOpenDialog(getTopComponent());
    return fileChooser.getSelectedFile();
  }

  public void setImportCatalogAvailable(boolean b) {
    importCatalog.setEnabled(b);
  }

  public void importSuccessful() {
    JOptionPane.showMessageDialog(getTopComponent(), "Katalog erfolgreich aktualisiert!");
  }

  public void progressStarted() {
    JOptionPane.showMessageDialog(getTopComponent(), "Katalog wird aktualisiert!");
  }

  public void surchargeGroupsSet() {
    JOptionPane.showMessageDialog(
        getTopComponent(), "Die Zuschlagsgruppen wurden erfolgreich gesetzt!");
  }

  public void setAllDiffs(MappedDifferences[] mappedDiffs) {
    difference.setItems(Arrays.asList(mappedDiffs));
  }

  public Difference<Article, ?> getSelectedDiff() {
    return difference.getSelected();
  }

  public void mergeDiffsFirst() {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Bitte korrigiere alle Konflikte bevor du den Katalog in die Datenbank übernimmst.");
    differences.setRowFilter(null);
  }
}
