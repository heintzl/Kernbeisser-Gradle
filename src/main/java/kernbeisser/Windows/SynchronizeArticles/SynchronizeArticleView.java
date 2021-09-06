package kernbeisser.Windows.SynchronizeArticles;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.Color;
import java.awt.Insets;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.CustomComponents.ObjectTable.Adjustors.IconCustomizer;
import kernbeisser.CustomComponents.ObjectTable.Adjustors.SimpleCellAdjustor;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.Columns.CustomizableColumn;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.TextFields.DoubleParseField;
import kernbeisser.Enums.Colors;
import kernbeisser.Enums.Setting;
import kernbeisser.Security.Utils.Getter;
import kernbeisser.Tasks.Catalog.Merge.ArticleDifference;
import kernbeisser.Tasks.Catalog.Merge.ArticleMerge;
import kernbeisser.Tasks.Catalog.Merge.Difference;
import kernbeisser.Tasks.Catalog.Merge.MappedDifference;
import kernbeisser.Tasks.Catalog.Merge.Solution;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class SynchronizeArticleView implements IView<SynchronizeArticleController> {

  private JPanel main;
  private JButton back;
  private ObjectTable<ArticleMerge> differences;
  private JButton filter;
  private JButton useKernbeisser;
  private JButton useKornkraft;
  private JButton selectAll;
  private JButton removeSelection;
  private JButton importCatalog;
  private JButton autoLinkCatalogSurchargeGroups;
  private AdvancedComboBox<MappedDifference> differenceFilter;
  private DoubleParseField maxAllowedDiff;
  private JButton useKernbeisserAndIgnore;
  private JProgressBar progressBar;
  private JLabel progressName;
  private JPanel progress;
  private JButton importCatalogFromInternet;

  @Linked
  private SynchronizeArticleController controller;

  @Override
  public void initialize(SynchronizeArticleController controller) {
    back.addActionListener(e -> back());
    differenceFilter.addActionListener(e -> initForDiff(getSelectedFilter()));
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
    importCatalog.addActionListener(e -> controller.importCatalogFile());
    importCatalogFromInternet.addActionListener(e -> controller.importCatalogFromInternet());
    autoLinkCatalogSurchargeGroups.addActionListener(e -> controller.setProductGroups());
    importCatalogFromInternet.setVisible(Setting.UPDATE_CATALOG_FROM_INTERNET.getBooleanValue());
    differences.setRowFilter(articleMerge -> !articleMerge.isResolved());
    differenceFilter.getModel().setAllowNullSelection(true);
    differenceFilter.getRenderer().setNoSelectionText("Alle Kategorien");
    initForDiff(MappedDifference.values());
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private void createUIComponents() {
    differences = new ObjectTable<>();
    maxAllowedDiff = new DoubleParseField();
    differenceFilter = new AdvancedComboBox<>(Difference::getName);
  }

  void setDifferences(Collection<ArticleMerge> articleDifference) {
    differences.setObjects(articleDifference);
    if (articleDifference.size() > 0) {
      differences.getSelectionModel().setSelectionInterval(0, 0);
    }
  }

  private void initForDiff(@NotNull MappedDifference... mappedDifferences) {
    differences.setColumns(
        Columns.create("Artikel", (Getter<ArticleMerge, Object>) e -> e.getRevision().getName()),
        Columns.create(
            "Kornkraftnummer",
            (Getter<ArticleMerge, Object>) e -> e.getNewState().getSuppliersItemNumber()));
    for (MappedDifference mappedDifference : mappedDifferences) {
      SimpleCellAdjustor<ArticleMerge> diffColorMarker =
          (comp, articleMerge) ->
              comp.setForeground(
                  Objects.equals(
                      mappedDifference.get(articleMerge.getRevision()),
                      mappedDifference.get(articleMerge.getNewState()))
                      ? Colors.LABEL_FOREGROUND.getColor()
                      : new Color(0xF15959));
      differences.addColumn(
          new CustomizableColumn<ArticleMerge>(
              mappedDifference.getName() + " alt", e -> mappedDifference.get(e.getRevision()))
              .withCellAdjustor(diffColorMarker));
      differences.addColumn(
          new CustomizableColumn<ArticleMerge>(
              mappedDifference.getName() + " neu", e -> mappedDifference.get(e.getNewState()))
              .withCellAdjustor(diffColorMarker));
      differences.addColumn(
          new CustomizableColumn<ArticleMerge>(
              "Unterschied",
              e ->
                  String.format(
                      "%.2f%%",
                      mappedDifference.distance(
                          mappedDifference.get(e.getRevision()),
                          mappedDifference.get(e.getNewState()))
                          * 100))
              .withCellAdjustor(new IconCustomizer<>(e -> getIcon(e, mappedDifference))));
    }
    filterTable();
  }

  public void filterTable() {
    MappedDifference[] mappedDifferences = getSelectedFilter();
    differences.setRowFilter(
        articleMerge -> {
          if (articleMerge.isResolved()) {
            return false;
          }
          return articleMerge.containsConflict(mappedDifferences);
        });
  }

  private final Icon RESOLVED =
      IconFontSwing.buildIcon(
          FontAwesome.CHECK, Tools.scaleWithLabelScalingFactor(15), new Color(0x2AC9AC));

  public Icon getIcon(ArticleMerge merge, MappedDifference difference) {
    if (merge.getArticleDifferences().stream()
        .filter(e -> e.getArticleDifference().equals(difference))
        .map(ArticleDifference::getSolution)
        .noneMatch(e -> e.equals(Solution.NO_SOLUTION))) {
      return RESOLVED;
    }
    return null;
  }

  MappedDifference[] getSelectedFilter() {
    return differenceFilter
        .getSelected()
        .map(v -> new MappedDifference[]{v})
        .orElse(MappedDifference.values());
  }

  public double getAllowedDifference() {
    return maxAllowedDiff.getSafeValue() / 100;
  }

  public void setObjectFilter() {
  }

  Collection<ArticleMerge> getSelectedObjects() {
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

  public void surchargeGroupsSet() {
    JOptionPane.showMessageDialog(
        getTopComponent(), "Die Zuschlagsgruppen wurden erfolgreich gesetzt!");
  }

  public void setAllDiffs(MappedDifference[] mappedDiffs) {
    differenceFilter.setItems(Arrays.asList(mappedDiffs));
  }

  public void mergeDiffsFirst() {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Bitte korrigiere alle Konflikte bevor du den Katalog in die Datenbank übernimmst.");
    differenceFilter.setSelectedItem(null);
    initForDiff(getSelectedFilter());
    filterTable();
  }

  public void showProgress(String progressName) {
    this.progressName.setText(progressName);
    progress.setVisible(true);
  }

  public void progressFinished() {
    progress.setVisible(false);
  }

  public void removeAll(Collection<ArticleMerge> collection) {
    differences.removeAll(collection);
  }

  public String messageRequestInputURL() {
    return Optional.ofNullable(
            JOptionPane.showInputDialog("Bitte geben sie die Kornkraft BNN-Datei URL ein:"))
        .orElseThrow(CancellationException::new);
  }

  public void messageInvalidURL() {
    message("Die eingegebene URL ist nicht korrekt!", "URL nicht korrekt!");
  }

}
