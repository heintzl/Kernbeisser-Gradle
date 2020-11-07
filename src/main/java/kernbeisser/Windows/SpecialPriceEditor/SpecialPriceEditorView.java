package kernbeisser.Windows.SpecialPriceEditor;

import java.awt.*;
import java.time.Instant;
import java.time.YearMonth;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.*;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.PermissionCheckBox;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.CustomComponents.TextFields.DateParseField;
import kernbeisser.CustomComponents.TextFields.DoubleParseField;
import kernbeisser.DBEntities.ArticleBase;
import kernbeisser.DBEntities.Offer;
import kernbeisser.Enums.Repeat;
import kernbeisser.Exeptions.IncorrectInput;
import kernbeisser.Useful.Date;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class SpecialPriceEditorView implements IView<SpecialPriceEditorController> {
  private ObjectTable<Offer> offers;

  private kernbeisser.CustomComponents.TextFields.DateParseField from;
  private kernbeisser.CustomComponents.TextFields.DateParseField to;
  private JComboBox<Repeat> repeat;
  private SearchBoxView<ArticleBase> searchBox;
  private DoubleParseField specialNetPrice;
  private JButton remove;
  private JButton add;
  private JButton edit;
  private JPanel main;
  private JButton finishButton;
  private JButton searchFrom;
  private JButton searchTo;
  private JLabel selectedArticle;
  private PermissionCheckBox filterActionArticle;
  private JLabel selectedArticleNetPrice;
  private ObjectTable<Offer> offersMonth;
  private JComboBox<YearMonth> month;

  @Linked private SearchBoxController<ArticleBase> searchBoxController;
  @Linked private SpecialPriceEditorController controller;
  @Linked private AtomicReference<Boolean> filterOnlyActionArticle;

  void fillRepeat(Repeat[] repeats) {
    repeat.removeAllItems();
    for (Repeat r : repeats) {
      repeat.addItem(r);
    }
  }

  void setOffers(Collection<Offer> offers) {
    this.offers.setObjects(offers);
  }

  void setFrom(Instant s) {
    from.setValue(s);
  }

  void setTo(Instant s) {
    to.setValue(s);
  }

  void setRepeat(Repeat r) {
    repeat.setSelectedItem(r);
  }

  void setSpecialNetPrice(double p) {
    specialNetPrice.setText(String.format("%.2f", p));
    if (p == 0) specialNetPrice.setText("");
  }

  void setEditEnable(boolean b) {
    edit.setEnabled(b);
  }

  void setRemoveEnable(boolean b) {
    remove.setEnabled(b);
  }

  void setSelectedArticleIdentifier(String name) {
    selectedArticle.setText(name == null ? "Kein Artikel ausgewählt" : name);
  }

  void setSelectedArticleNetPrice(double d) {
    selectedArticleNetPrice.setText(String.format("Normalpreis: %.2f€", d));
  }

  boolean filterOnlyActionArticle() {
    return filterActionArticle.isSelected();
  }

  private void createUIComponents() {
    searchBox = searchBoxController.getView();
    offers =
        new ObjectTable<>(
            Column.create("Von", e -> Date.INSTANT_DATE.format(e.getFromDate())),
            Column.create("Bis", e -> Date.INSTANT_DATE.format(e.getToDate())),
            Column.create("Aktionsnettopreis", Offer::getSpecialNetPrice),
            Column.create("Wiederholung", Offer::getRepeatMode));
    offersMonth =
        new ObjectTable<Offer>(
            Column.create("Artikel", Offer::getArticle),
            Column.create("Von", e -> Date.INSTANT_DATE.format(e.getFromDate())),
            Column.create("Bis", e -> Date.INSTANT_DATE.format(e.getToDate())),
            Column.create("Aktionsnettopreis", Offer::getSpecialNetPrice),
            Column.create("Wiederholung", Offer::getRepeatMode));
    offersMonth.addSelectionListener(
        e -> searchBox.selectObject(a -> a.getId() == e.getArticle().getId()));
    from = new DateParseField();
    to = new DateParseField();
  }

  public void setOffersMonth(Collection<Offer> offersMonth) {
    this.offersMonth.setObjects(offersMonth);
  }

  Offer getSelectedOffer() {
    return offers.getSelectedObject();
  }

  public Instant getFrom() throws IncorrectInput {
    return from.getUncheckedValue();
  }

  public Instant getTo() throws IncorrectInput {
    return to.getUncheckedValue();
  }

  double getSpecialPrice() {
    return specialNetPrice.getSafeValue();
  }

  Repeat getRepeatMode() {
    return (Repeat) repeat.getSelectedItem();
  }

  public void setAddEnable(boolean b) {
    add.setEnabled(b);
  }

  public void cannotParseDateFormat() {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Das angegebene Datum kann nicht eingelesen werden,\n bitte überprüfen sie ob das folgende Format eingehalten wurde:\n dd:mm:yyyy");
  }

  @Override
  public void initialize(SpecialPriceEditorController controller) {
    int ICON_SIZE = 15;
    add.setIcon(IconFontSwing.buildIcon(FontAwesome.PLUS, ICON_SIZE, Color.GREEN));
    remove.setIcon(IconFontSwing.buildIcon(FontAwesome.TRASH, ICON_SIZE, Color.RED));
    edit.setIcon(IconFontSwing.buildIcon(FontAwesome.PENCIL, ICON_SIZE, new Color(0x757EFF)));
    offers.addSelectionListener(controller::selectOffer);
    add.addActionListener(e -> controller.add());
    edit.addActionListener(e -> controller.edit());
    remove.addActionListener(e -> controller.remove());
    finishButton.addActionListener(e -> back());
    searchFrom.addActionListener(e -> controller.searchFrom());
    searchTo.addActionListener(e -> controller.searchTo());
    searchTo.setIcon(IconFontSwing.buildIcon(FontAwesome.CALENDAR, ICON_SIZE, Color.GRAY));
    searchFrom.setIcon(IconFontSwing.buildIcon(FontAwesome.CALENDAR, ICON_SIZE, Color.GRAY));
    filterActionArticle.addActionListener(
        e -> {
          filterOnlyActionArticle.set(filterActionArticle.isSelected());
          controller.refreshSearchSolutions();
        });
    offers.addSelectionListener(e -> controller.load(e.getArticle()));
    month.addActionListener(e -> controller.loadMonth());
  }

  public void setMonth(YearMonth[] months) {
    month.removeAllItems();
    for (YearMonth m : months) {
      this.month.addItem(m);
    }
  }

  @Override
  public @NotNull Dimension getSize() {
    return new Dimension(670, 600);
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  @Override
  public String getTitle() {
    return "Aktionsartikel bearbeiten";
  }

  public boolean commitStrangeNetPrice() {
    return JOptionPane.showConfirmDialog(
            getTopComponent(),
            "Der eingengebene Aktionspreis ist höher als der Normalpreis. Ist das wirklich korrekt?")
        == 0;
  }

  public YearMonth getMonth() {
    return (YearMonth) month.getSelectedItem();
  }
}
