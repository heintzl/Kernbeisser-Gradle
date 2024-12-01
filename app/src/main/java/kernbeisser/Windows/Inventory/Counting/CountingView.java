package kernbeisser.Windows.Inventory.Counting;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.TextFields.DoubleParseField;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.ArticleStock;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.Enums.UserSetting;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class CountingView implements IView<CountingController> {
  private JPanel main;
  private JButton commit;
  private DoubleParseField amount;
  private JButton apply;
  private JLabel articleName;
  private ObjectTable<ArticleStock> articleStocks;
  private AdvancedComboBox<Shelf> shelf;
  private JLabel articleNumber;
  private JButton addArticle;
  private ArticleStock stockBefore;
  JLabel inventoryDate;
  private JLabel unit;
  private JButton editThresholds;

  @Linked private CountingController controller;

  @Override
  public void initialize(CountingController controller) {
    shelf.addSelectionListener(controller::loadShelf);
    apply.addActionListener(e -> tryApplyAmount());
    amount.addActionListener(e -> tryApplyAmount());
    addArticle.addActionListener(e -> controller.addArticleStock());
    articleStocks.addSelectionListener(this::stockSelectionChanged);
    articleStocks.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    commit.addActionListener(e -> close());
    editThresholds.addActionListener(e -> editThresholds());
  }

  Optional<Shelf> getSelectedShelf() {
    return shelf.getSelected();
  }

  void setSelectedShelf(Shelf shelf) {
    this.shelf.setSelectedItem(shelf);
  }

  void setArticleStocks(Collection<ArticleStock> articleStocks) {
    this.articleStocks.setObjects(articleStocks);
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private void createUIComponents() {
    articleStocks =
        new ObjectTable<>(
            Columns.<ArticleStock>create("Artikelnummer", e -> e.getArticle().getKbNumber())
                .withSorter(Column.NUMBER_SORTER),
            Columns.<ArticleStock>create("Artikelname", e -> e.getArticle().getName())
                .withPreferredWidth(700),
            Columns.<ArticleStock>create(
                    "Gezählte Menge", (e -> String.format("%.1f", e.getCounted())))
                .withSorter(Column.NUMBER_SORTER),
            Columns.create("Einh.", controller::getCountingUnit).withPreferredWidth(30));
    shelf = new AdvancedComboBox<>(e -> e.getShelfNo() + " - " + e.getLocation());
    amount = new DoubleParseField(0.0, 999999.9);
  }

  void tryApplyAmount() {
    articleStocks.getSelectedObject().ifPresent(e -> applyAmount(e, amount.getSafeValue()));
  }

  void applyAmount(ArticleStock stock, double value) {
    if (!controller.setStock(stock, value)) {
      return;
    }
    int selectedIndex = articleStocks.getSelectionModel().getMinSelectionIndex();
    if (selectedIndex == articleStocks.getObjects().size()) {
      Tools.beep();
      return;
    }
    articleStocks.getSelectionModel().setSelectionInterval(selectedIndex + 1, selectedIndex + 1);
    articleStocks.getSelectedObject().ifPresent(this::loadArticleStock);
  }

  private void saveStockBefore() {
    double count = amount.getSafeValue();
    if (stockBefore != null && stockBefore.getCounted() != count) {
      controller.setStock(stockBefore, count);
    }
  }

  void stockSelectionChanged(ArticleStock stock) {
    saveStockBefore();
    loadArticleStock(stock);
  }

  void loadArticleStock(ArticleStock stock) {
    stockBefore = stock;
    amount.setText(String.valueOf(stock.getCounted()));
    articleName.setText(stock.getArticle().getName());
    articleNumber.setText(String.valueOf(stock.getArticle().getKbNumber()));
    Article article = stock.getArticle();
    unit.setText(controller.getCountingUnit(stock));
    amount.requestFocus();
    amount.setSelectionStart(0);
    amount.setSelectionEnd(amount.getText().length());
  }

  public void setShelves(Collection<Shelf> allShelves) {
    shelf.setItems(allShelves);
  }

  public void selectFirst() {
    if (!articleStocks.getObjects().isEmpty()) {
      articleStocks.selectRow(0);
      ArticleStock stock = articleStocks.getSelectedObject().orElse(null);
      loadArticleStock(stock);
    }
  }

  public void selectArticle(Article article) {
    int rowIndex = 0;
    for (ArticleStock stock : articleStocks.getObjects()) {
      if (stock.getArticle().equals(article)) {
        loadArticleStock(stock);
        break;
      }
      rowIndex++;
    }
    articleStocks.selectRow(rowIndex);
  }

  public void refreshArticleStock(ArticleStock articleStock) {
    articleStocks.replace(articleStock, articleStock);
  }

  private void close() {
    saveStockBefore();
    controller.runOnClose();
    back();
  }

  @Override
  public IconCode getTabIcon() {
    return FontAwesome.LIST;
  }

  @Override
  public String getTitle() {
    return "Zähl Ergebnisse eingeben";
  }

  public void setInventoryDate(String dateString) {
    this.inventoryDate.setText(dateString);
  }

  public void formatInventoryDateAsWarning() {
    this.inventoryDate.setForeground(Color.RED);
    Font font =
        this.inventoryDate
            .getFont()
            .deriveFont(
                Collections.singletonMap(TextAttribute.WEIGHT, TextAttribute.WEIGHT_EXTRABOLD));
    this.inventoryDate.setFont(font);
  }

  public void formatInventoryDateAsMessage() {
    this.inventoryDate.setForeground(Color.BLACK);
    Font font =
        this.inventoryDate
            .getFont()
            .deriveFont(
                Collections.singletonMap(TextAttribute.WEIGHT, TextAttribute.WEIGHT_SEMIBOLD));
    this.inventoryDate.setFont(font);
  }

  public boolean confirmLowWeighableAmountWarning(double value) {
    Tools.beep();
    return JOptionPane.showConfirmDialog(
            getContent(),
            "Für Auswiegeware ist %.1f ein sehr niedriger Wert.\n".formatted(value)
                + "Willst Du den Wert anpassen?",
            "Ungewöhnlich niedriger Wert",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE)
        == JOptionPane.NO_OPTION;
  }

  public boolean confirmHighPieceAmountWarning(double value) {
    Tools.beep();
    return JOptionPane.showConfirmDialog(
            getContent(),
            "Für Stückware ist %.1f eine sehr große Anzahl.\n".formatted(value)
                + "Willst Du den Wert anpassen?",
            "Ungewöhnlich großer Wert",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE)
        == JOptionPane.NO_OPTION;
  }

  private void editThresholds() {

    float scaleFactor = UserSetting.FONT_SCALE_FACTOR.getFloatValue(LogInModel.getLoggedIn());
    int preferredHeight = (int) (24.0 * scaleFactor);
    Dimension labelSize = new Dimension((int) (250.0 * scaleFactor), preferredHeight);
    Dimension textFieldSize = new Dimension((int) (70.0 * scaleFactor), preferredHeight);
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    JPanel weighablePanel = new JPanel();
    weighablePanel.setLayout(new BoxLayout(weighablePanel, BoxLayout.X_AXIS));
    JPanel piecePanel = new JPanel();
    piecePanel.setLayout(new BoxLayout(piecePanel, BoxLayout.X_AXIS));

    DoubleParseField weighableThreshold = new DoubleParseField();
    weighableThreshold.setText(controller.getWeighableThreshold());
    weighableThreshold.setPreferredSize(textFieldSize);
    weighableThreshold.setHorizontalAlignment(SwingConstants.RIGHT);
    JLabel weighableLabel = new JLabel("Minimum für Auswiegeware: ");
    weighableLabel.setPreferredSize(new Dimension(labelSize));

    DoubleParseField pieceThreshold = new DoubleParseField();
    pieceThreshold.setText(controller.getPieceThreshold());
    pieceThreshold.setPreferredSize(new Dimension(textFieldSize));
    pieceThreshold.setHorizontalAlignment(SwingConstants.RIGHT);
    JLabel pieceLabel = new JLabel("Maximum für Stück: ");
    pieceLabel.setPreferredSize(new Dimension(labelSize));

    weighablePanel.add(weighableLabel);
    weighablePanel.add(weighableThreshold);
    piecePanel.add(pieceLabel);
    piecePanel.add(pieceThreshold);
    panel.add(weighablePanel);
    panel.add(piecePanel);
    int response =
        JOptionPane.showConfirmDialog(
            getContent(), panel, "Grenzwerte für Warnungen", JOptionPane.OK_CANCEL_OPTION);
    if (response == JOptionPane.OK_OPTION) {
      controller.applyThresholds(weighableThreshold.getSafeValue(), pieceThreshold.getSafeValue());
    }
  }

  // @spotless:off

  {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
    $$$setupUI$$$();
  }

  /**
   * Method generated by IntelliJ IDEA GUI Designer
   * >>> IMPORTANT!! <<<
   * DO NOT edit this method OR call it in your code!
   *
   * @noinspection ALL
   */
  private void $$$setupUI$$$() {
    createUIComponents();
    main = new JPanel();
    main.setLayout(new GridLayoutManager(6, 7, new Insets(5, 5, 5, 5), -1, -1));
    commit = new JButton();
    commit.setText("Eingabe abschließen");
    main.add(commit, new GridConstraints(5, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final Spacer spacer1 = new Spacer();
    main.add(spacer1, new GridConstraints(5, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    main.add(amount, new GridConstraints(5, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    apply = new JButton();
    apply.setText("Übernehmen");
    main.add(apply, new GridConstraints(5, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JLabel label1 = new JLabel();
    label1.setText("Bestand von ");
    main.add(label1, new GridConstraints(3, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    articleNumber = new JLabel();
    articleNumber.setText("");
    main.add(articleNumber, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    final JScrollPane scrollPane1 = new JScrollPane();
    main.add(scrollPane1, new GridConstraints(2, 0, 1, 7, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(10, 109), null, 0, false));
    scrollPane1.setViewportView(articleStocks);
    final JLabel label2 = new JLabel();
    label2.setText("Regal:");
    main.add(label2, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    main.add(shelf, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    inventoryDate = new JLabel();
    Font inventoryDateFont = this.$$$getFont$$$(null, Font.BOLD, -1, inventoryDate.getFont());
    if (inventoryDateFont != null) inventoryDate.setFont(inventoryDateFont);
    inventoryDate.setText("");
    main.add(inventoryDate, new GridConstraints(1, 2, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    addArticle = new JButton();
    addArticle.setText("Artikel der Liste hinzufügen");
    main.add(addArticle, new GridConstraints(3, 5, 1, 2, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    articleName = new JLabel();
    articleName.setText("");
    main.add(articleName, new GridConstraints(4, 1, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    unit = new JLabel();
    unit.setText("Stk.");
    main.add(unit, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(60, -1), new Dimension(60, -1), new Dimension(100, -1), 0, false));
    editThresholds = new JButton();
    editThresholds.setText("Warnungen...");
    editThresholds.setToolTipText("Grenzwerte für Warnungen einstellen");
    main.add(editThresholds, new GridConstraints(5, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
  }

  /**
   * @noinspection ALL
   */
  private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
    if (currentFont == null) return null;
    String resultName;
    if (fontName == null) {
      resultName = currentFont.getName();
    } else {
      Font testFont = new Font(fontName, Font.PLAIN, 10);
      if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
        resultName = fontName;
      } else {
        resultName = currentFont.getName();
      }
    }
    Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
    Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
    return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
  }

  /**
   * @noinspection ALL
   */
  public JComponent $$$getRootComponent$$$() {
    return main;
  }

  // @spotless:on
}
