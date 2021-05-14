package kernbeisser.Windows.ArticleOffersEditor;

import java.awt.*;
import java.time.YearMonth;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.*;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.PermissionCheckBox;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Offer;
import kernbeisser.Enums.Repeat;
import kernbeisser.Forms.ObjectForm.Components.AccessCheckingComboBox;
import kernbeisser.Forms.ObjectForm.Components.AccessCheckingDatePicker;
import kernbeisser.Forms.ObjectForm.Components.AccessCheckingField;
import kernbeisser.Forms.ObjectForm.Components.DataListener;
import kernbeisser.Forms.ObjectForm.ObjectForm;
import kernbeisser.Useful.Date;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import lombok.Getter;
import org.jdatepicker.JDateComponentFactory;
import org.jetbrains.annotations.NotNull;

public class ArticleOffersEditorView implements IView<ArticleOffersEditorController> {
  private ObjectTable<Offer> offers;

  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingComboBox<Offer, Repeat> repeat;
  private SearchBoxView<Article> searchBox;
  private kernbeisser.Forms.ObjectForm.Components.AccessCheckingField<Offer, Double>
      specialNetPrice;
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
  private JButton printMonth;
  private AccessCheckingDatePicker<Offer> fromDate;
  private AccessCheckingDatePicker<Offer> toDate;

  @Linked private SearchBoxController<Article> searchBoxController;
  @Linked private ArticleOffersEditorController controller;
  @Linked private AtomicReference<Boolean> filterOnlyActionArticle;

  @Getter private ObjectForm<Offer> objectForm;

  void fillRepeat(Repeat[] repeats) {
    repeat.removeAllItems();
    for (Repeat r : repeats) {
      repeat.addItem(r);
    }
  }

  void setOffers(Collection<Offer> offers) {
    final Offer offer = this.offers.getSelectedObject().orElse(null);
    this.offers.setObjects(offers);
    int index = this.offers.getObjects().indexOf(offer);
    if (index != -1) this.offers.getSelectionModel().setSelectionInterval(index, index);
  }

  void setSpecialNetPrice(double p) {
    specialNetPrice.setText(String.format("%.2f", p));
    if (p == 0) specialNetPrice.setText("");
  }

  void setSelectedArticleIdentifier(String name) {
    selectedArticle.setText(name == null ? "Kein Artikel ausgewählt" : name);
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
    fromDate = new AccessCheckingDatePicker<>(Offer::getFromDate, Offer::setFromDate, true);
    toDate = new AccessCheckingDatePicker<>(Offer::getToDate, Offer::setToDate, false);
    specialNetPrice =
        new AccessCheckingField<>(
            Offer::getSpecialNetPrice,
            Offer::setSpecialNetPrice,
            AccessCheckingField.DOUBLE_FORMER);
    repeat =
        new AccessCheckingComboBox<>(
            Offer::getRepeatMode, Offer::setRepeatMode, controller.getRepeatModes());
    JDateComponentFactory factory = new JDateComponentFactory();
    factory.createJDatePicker();
  }

  public void setOffersMonth(Collection<Offer> offersMonth) {
    this.offersMonth.setObjects(offersMonth);
  }

  Optional<Offer> getSelectedOffer() {
    return offers.getSelectedObject();
  }

  public void setAddEnable(boolean b) {
    add.setEnabled(b);
  }

  @Override
  public void initialize(ArticleOffersEditorController controller) {
    int ICON_SIZE = 15;
    add.setIcon(IconFontSwing.buildIcon(FontAwesome.PLUS, ICON_SIZE, Color.GREEN));
    remove.setIcon(IconFontSwing.buildIcon(FontAwesome.TRASH, ICON_SIZE, Color.RED));
    edit.setIcon(IconFontSwing.buildIcon(FontAwesome.PENCIL, ICON_SIZE, new Color(0x757EFF)));
    add.addActionListener(e -> controller.add());
    edit.addActionListener(e -> controller.edit());
    remove.addActionListener(e -> controller.remove());
    finishButton.addActionListener(e -> back());
    filterActionArticle.addActionListener(
        e -> {
          filterOnlyActionArticle.set(filterActionArticle.isSelected());
          controller.invokeSearch();
        });
    month.addActionListener(e -> controller.loadMonth());
    printMonth.addActionListener(e -> controller.printMonth());
    objectForm =
        new ObjectForm<>(
            fromDate,
            toDate,
            specialNetPrice,
            repeat,
            new DataListener<>(
                offer -> offer.getArticle().getNetPrice(),
                e -> selectedArticleNetPrice.setText(String.format("Normalpreis: %.2f€", e))),
            new DataListener<>(Offer::getArticle, a -> setSelectedArticleIdentifier(a.getName())));
    offers.addSelectionListener(objectForm::setSource);
    searchBoxController.addLostSelectionListener(controller::lostContact);
    objectForm.registerObjectValidator(controller::validateOffer);
    searchBoxController.addSelectionListener(controller::load);
    offers.addSelectionListener(controller::loadOffer);
    offersMonth.addSelectionListener(controller::loadOffer);
    objectForm.setObjectDistinction("Das Angebot");
  }

  void setModifyEnable(boolean b) {
    edit.setEnabled(b);
    remove.setEnabled(b);
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
            "Der eingegebene Aktionspreis ist höher als der Normalpreis. Ist das wirklich korrekt?")
        == 0;
  }

  public YearMonth getMonth() {
    return (YearMonth) month.getSelectedItem();
  }

  public void messageSelectOffer() {
    JOptionPane.showMessageDialog(getContent(), "Bitte wählen sie vorher ein Angebot aus.");
  }

  public void messageSelectArticle() {
    JOptionPane.showMessageDialog(getContent(), "Bitte wählen sie vorher ein Angebot aus.");
  }

  public void messageInvalidTimeSpace() {
    message(
        "Der gewählte Zeitraum ist nicht korrekt! Das Startdatum muss vor dem Enddatum liegen.");
  }
}
