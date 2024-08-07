package kernbeisser.Windows.EditArticles;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.*;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import jiconfont.icons.font_awesome.FontAwesome;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.Article;
import kernbeisser.Enums.ArticleDeletionResult;
import kernbeisser.Forms.ObjectView.ObjectViewView;
import kernbeisser.Useful.Icons;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class EditArticlesView implements IView<EditArticlesController> {

  private JPanel main;
  private JButton choosePriceList;
  private ObjectViewView<Article> objectView;

  @Linked private EditArticlesController controller;

  public void messageBarcodeNotFound(String s) {
    Tools.beep();
    JOptionPane.showMessageDialog(
        getContent(),
        "Konnte keinen Artikel mit Barcode \"" + s + "\" finden",
        "Artikel nicht gefunden",
        JOptionPane.INFORMATION_MESSAGE);
  }

  public void messageNoDifferences() {
    message(
        "Es gibt keine bekannten Differenzen. Du musst erst welche anzeigen, bevor sie übernommen werden können.",
        "Fehlende Differenz-Daten");
  }

  public void messageNoSelection() {
    message(
        "Du musst die Artikel auswählen, für die die Katalogdaten übernommen werden sollen.",
        "Fehlende Artikel-Auswahl");
  }

  @Override
  public void initialize(EditArticlesController controller) {
    choosePriceList.addActionListener(e -> controller.openPriceListSelection());
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  @Override
  public String getTitle() {
    return "Artikel bearbeiten";
  }

  private void createUIComponents() {
    objectView = controller.getObjectView();
  }

  public void showLog(List<String> mergeLog) {
    ObjectTable<String> log = new ObjectTable<>(mergeLog, Columns.create("Meldung", e -> e));
    JScrollPane logPanel = new JScrollPane(log);
    Dimension thisSize = getSize();
    logPanel.setPreferredSize(
        new Dimension((int) (thisSize.getWidth() * 0.7), (int) (thisSize.getHeight() * 0.7)));
    JOptionPane.showMessageDialog(
        getContent(), logPanel, "Katalog-Übernahme-Ergebnis", JOptionPane.INFORMATION_MESSAGE);
  }

  private static void showArticleList(Component parentComponent, List<Article> articles) {
    ObjectTable<Article> table =
        new ObjectTable<>(
            articles,
            Columns.create("kbNumber", Article::getKbNumber)
                .withSorter(Column.NUMBER_SORTER)
                .withPreferredWidth(100),
            Columns.create("Artikel", Article::getName).withPreferredWidth(500));
    JOptionPane.showMessageDialog(
        parentComponent, new JScrollPane(table), "Artikel", JOptionPane.INFORMATION_MESSAGE);
  }

  private static final Icon DELETE_ICON =
      Icons.defaultIcon(FontAwesome.TRASH, new Color(126, 16, 0));
  private static final Icon DISCONTINUE_ICON =
      Icons.defaultIcon(FontAwesome.RECYCLE, new Color(40, 151, 0));
  private static final Icon KEEP_ICON =
      Icons.defaultIcon(FontAwesome.STOP_CIRCLE, new Color(69, 69, 69));

  private static Icon deletionIcon(ArticleDeletionResult result) {
    switch (result) {
      case DELETE -> {
        return DELETE_ICON;
      }
      case DISCONTINUE -> {
        return DISCONTINUE_ICON;
      }
      default -> {
        return KEEP_ICON;
      }
    }
  }

  public static boolean confirmDelete(
      Component parentComponent, Map<ArticleDeletionResult, List<Article>> results) {
    ObjectTable<ArticleDeletionResult> table =
        new ObjectTable<>(
            results.keySet().stream().sorted().toList(),
            Columns.createIconColumn("", EditArticlesView::deletionIcon),
            Columns.create("Ergebnis", ArticleDeletionResult::toString).withPreferredWidth(400),
            Columns.<ArticleDeletionResult>create("Anzahl", e -> results.get(e).size())
                .withPreferredWidth(50)
                .withSorter(Column.NUMBER_SORTER),
            Columns.createIconColumn(
                Icons.defaultIcon(FontAwesome.TABLE, Color.BLUE),
                e -> showArticleList(parentComponent, results.get(e)),
                e -> !results.get(e).isEmpty()));

    JPanel labelPanel = new JPanel();
    labelPanel.add(new JLabel(DELETE_ICON));
    labelPanel.add(new JLabel(DISCONTINUE_ICON));
    JLabel label =
        new JLabel("Die so gekennzeichneten Artikel werden  aus dem Artikelstamm entfernt");
    label.setFont(label.getFont().deriveFont(Font.ITALIC));
    labelPanel.add(label);

    JPanel tablePanel = new JPanel(new BorderLayout());
    tablePanel.add(table, BorderLayout.CENTER);
    tablePanel.add(labelPanel, BorderLayout.SOUTH);
    return JOptionPane.showConfirmDialog(
            parentComponent,
            tablePanel,
            "Artikel entfernen",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE)
        == JOptionPane.OK_OPTION;
  }

  // @spotless:off

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /** Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        main = new JPanel();
        main.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        main.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, new Dimension(-1, 50), 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Suche nach Ladennummer, Lieferantenartikelnummer, Barcode (die letzten vier Stellen) oder Preisliste");
        panel2.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        choosePriceList = new JButton();
        choosePriceList.setText("Preisliste auswählen");
        panel2.add(choosePriceList, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        main.add(spacer2, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        main.add(objectView.$$$getRootComponent$$$(), new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /** @noinspection ALL */
    public JComponent $$$getRootComponent$$$() {
        return main;
    }

    // @spotless:on
}
