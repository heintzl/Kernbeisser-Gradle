package kernbeisser.Windows.SynchronizeArticles;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.CustomComponents.ObjectTable.Adjustors.IconCustomizer;
import kernbeisser.CustomComponents.ObjectTable.Adjustors.SimpleCellAdjustor;
import kernbeisser.CustomComponents.ObjectTable.Column;
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
  private AdvancedComboBox<Filter> differenceFilter;
  private DoubleParseField maxAllowedDiff;
  private JButton useKernbeisserAndIgnore;
  private JProgressBar progressBar;
  private JLabel progressName;
  private JPanel progress;
  private JButton importCatalogFromInternet;
  private JButton cancel;

  @Linked private SynchronizeArticleController controller;

  {
    // GUI initializer generated by IntelliJ IDEA GUI Designer
    // >>> IMPORTANT!! <<<
    // DO NOT EDIT OR ADD ANY CODE HERE!
    $$$setupUI$$$();
  }

  /**
   * Method generated by IntelliJ IDEA GUI Designer >>> IMPORTANT!! <<< DO NOT edit this method OR
   * call it in your code!
   *
   * @noinspection ALL
   */
  private void $$$setupUI$$$() {
    createUIComponents();
    main = new JPanel();
    main.setLayout(new GridLayoutManager(5, 2, new Insets(5, 5, 5, 5), -1, -1));
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new GridLayoutManager(1, 6, new Insets(0, 0, 0, 0), -1, -1));
    main.add(
        panel1,
        new GridConstraints(
            4,
            0,
            1,
            2,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    back = new JButton();
    back.setText("Fertig");
    panel1.add(
        back,
        new GridConstraints(
            0,
            5,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final Spacer spacer1 = new Spacer();
    panel1.add(
        spacer1,
        new GridConstraints(
            0,
            3,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            1,
            null,
            null,
            null,
            0,
            false));
    importCatalog = new JButton();
    importCatalog.setText("Kornkraft Katalog importieren");
    panel1.add(
        importCatalog,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    autoLinkCatalogSurchargeGroups = new JButton();
    autoLinkCatalogSurchargeGroups.setText("Produktgruppen automatisch setzen");
    panel1.add(
        autoLinkCatalogSurchargeGroups,
        new GridConstraints(
            0,
            2,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    importCatalogFromInternet = new JButton();
    importCatalogFromInternet.setText("Kornkraft Katalog aus dem Internet impotieren");
    panel1.add(
        importCatalogFromInternet,
        new GridConstraints(
            0,
            1,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    cancel = new JButton();
    cancel.setText("Abbrechen");
    panel1.add(
        cancel,
        new GridConstraints(
            0,
            4,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JScrollPane scrollPane1 = new JScrollPane();
    main.add(
        scrollPane1,
        new GridConstraints(
            1,
            0,
            1,
            2,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
            null,
            null,
            null,
            0,
            false));
    scrollPane1.setViewportView(differences);
    final JPanel panel2 = new JPanel();
    panel2.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
    main.add(
        panel2,
        new GridConstraints(
            0,
            0,
            1,
            2,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    final JLabel label1 = new JLabel();
    label1.setText("Filtern");
    panel2.add(
        label1,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final Spacer spacer2 = new Spacer();
    panel2.add(
        spacer2,
        new GridConstraints(
            0,
            1,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            1,
            null,
            null,
            null,
            0,
            false));
    final JPanel panel3 = new JPanel();
    panel3.setLayout(new GridLayoutManager(2, 6, new Insets(0, 0, 0, 0), -1, -1));
    panel2.add(
        panel3,
        new GridConstraints(
            1,
            0,
            1,
            2,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    final JLabel label2 = new JLabel();
    label2.setText("Unterschied");
    panel3.add(
        label2,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JLabel label3 = new JLabel();
    label3.setText("Maximale Abweichung[%]");
    panel3.add(
        label3,
        new GridConstraints(
            0,
            1,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    filter = new JButton();
    filter.setText("Filtern");
    panel3.add(
        filter,
        new GridConstraints(
            1,
            2,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    selectAll = new JButton();
    selectAll.setText("Alle Auswählen");
    panel3.add(
        selectAll,
        new GridConstraints(
            1,
            3,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final Spacer spacer3 = new Spacer();
    panel3.add(
        spacer3,
        new GridConstraints(
            1,
            5,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            1,
            null,
            null,
            null,
            0,
            false));
    removeSelection = new JButton();
    removeSelection.setText("Auswahl entfernen");
    panel3.add(
        removeSelection,
        new GridConstraints(
            1,
            4,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    panel3.add(
        differenceFilter,
        new GridConstraints(
            1,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    panel3.add(
        maxAllowedDiff,
        new GridConstraints(
            1,
            1,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    final JPanel panel4 = new JPanel();
    panel4.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
    main.add(
        panel4,
        new GridConstraints(
            3,
            0,
            1,
            2,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    useKernbeisser = new JButton();
    useKernbeisser.setText("Auswahl von Kernbeisser übernehmen");
    panel4.add(
        useKernbeisser,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    useKornkraft = new JButton();
    useKornkraft.setText("Auswahl von Kornkraft übernehmen");
    panel4.add(
        useKornkraft,
        new GridConstraints(
            0,
            2,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final Spacer spacer4 = new Spacer();
    panel4.add(
        spacer4,
        new GridConstraints(
            0,
            3,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            1,
            null,
            null,
            null,
            0,
            false));
    useKernbeisserAndIgnore = new JButton();
    useKernbeisserAndIgnore.setText("Auswahl von Kernbeisser übernehmen und merken");
    panel4.add(
        useKernbeisserAndIgnore,
        new GridConstraints(
            0,
            1,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    progress = new JPanel();
    progress.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
    progress.setVisible(false);
    main.add(
        progress,
        new GridConstraints(
            2,
            0,
            1,
            2,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    progressBar = new JProgressBar();
    progressBar.setIndeterminate(true);
    progress.add(
        progressBar,
        new GridConstraints(
            1,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    progressName = new JLabel();
    progressName.setText("progress");
    progress.add(
        progressName,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
  }

  /**
   * @noinspection ALL
   */
  public JComponent $$$getRootComponent$$$() {
    return main;
  }

  private enum Filter {
    CONTAINER_SIZE_AND_PRICE(MappedDifference.CONTAINER_SIZE, MappedDifference.PRICE),
    PRICE(MappedDifference.PRICE),
    CONTAINER_SIZE(MappedDifference.CONTAINER_SIZE),
    AMOUNT(MappedDifference.AMOUNT),
    NAME(MappedDifference.NAME);
    @lombok.Getter private final MappedDifference[] differences;
    private final String displayText;

    Filter(String displayText, MappedDifference... differences) {
      this.displayText = displayText;
      this.differences = differences;
    }

    Filter(MappedDifference... differences) {
      this.displayText =
          Arrays.stream(differences).map(Difference::getName).collect(Collectors.joining(" & "));
      this.differences = differences;
    }

    String getDisplayText() {
      return displayText;
    }
  }

  @Override
  public void initialize(SynchronizeArticleController controller) {
    back.addActionListener(e -> back());
    differenceFilter.addActionListener(e -> initForDiff(getSelectedFilter()));
    selectAll.addActionListener(
        e -> {
          differences.requestFocusInWindow();
          differences.selectAll();
        });
    maxAllowedDiff.addActionListener(e -> filterTable());
    filter.addActionListener(e -> filterTable());
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
    cancel.addActionListener(controller::cancel);
    differenceFilter.setItems(Arrays.asList(Filter.values()));
    initForDiff(getSelectedFilter());
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private void createUIComponents() {
    differences = new ObjectTable<>();
    maxAllowedDiff = new DoubleParseField();
    differenceFilter = new AdvancedComboBox<>(Filter::getDisplayText);
  }

  void setDifferences(Collection<ArticleMerge> articleDifference) {
    differences.setObjects(articleDifference);
    if (articleDifference.size() > 0) {
      differences.getSelectionModel().setSelectionInterval(0, 0);
    }
  }

  private void initForDiff(@NotNull MappedDifference... mappedDifferences) {
    differences.setColumns(
        Columns.create("Artikel", (Getter<ArticleMerge, Object>) e -> e.getRevision().getName())
            .withColumnAdjustor(e -> e.setPreferredWidth(400)),
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
              .withSorter(Column.NUMBER_SORTER)
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
          for (MappedDifference mappedDifference : mappedDifferences) {
            Optional<ArticleDifference<?>> difference =
                articleMerge.getDifference(mappedDifference);
            if (difference.isPresent()) {
              ArticleDifference<?> articleDifference = difference.get();
              if (!articleDifference.hasSolution()
                  && isAllowedDifference(articleDifference.distance())) return true;
            }
          }
          return false;
        });
  }

  boolean isAllowedDifference(double max) {
    return Math.abs(max) <= getAllowedDifference();
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
        .map(Filter::getDifferences)
        .orElse(MappedDifference.values());
  }

  public double getAllowedDifference() {
    return maxAllowedDiff.getSafeValue() == 0.
        ? Double.MAX_VALUE
        : maxAllowedDiff.getSafeValue() / 100;
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

  public File requestInputFile(File defaultDir, String... extensions) {
    JFileChooser fileChooser = new JFileChooser(defaultDir);
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

  public String messageRequestInputURL(String initValue) {
    return Optional.ofNullable(
            JOptionPane.showInputDialog(
                "Bitte geben sie die Kornkraft BNN-Datei URL ein:", initValue))
        .orElseThrow(CancellationException::new);
  }

  public void messageInvalidURL() {
    message("Die eingegebene URL ist nicht korrekt!", "URL nicht korrekt!");
  }

  public boolean commitCancel() {
    return JOptionPane.showConfirmDialog(
            getTopComponent(),
            "Soll die akutelle Aktualiesierung des Kornkraft Kataloges abgebrochen werden?",
            "Konrkraft Katalog abbrechen?",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE)
        == 0;
  }
}
