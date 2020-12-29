package kernbeisser.Windows.SpecialPriceEditor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import kernbeisser.CustomComponents.DatePicker.DatePickerController;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Offer;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Repeat;
import kernbeisser.Exeptions.IncorrectInput;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;
import lombok.var;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

public class SpecialPriceEditorController
    extends Controller<SpecialPriceEditorView, SpecialPriceEditorModel> {

  @Linked private final SearchBoxController<Article> searchBoxController;

  @Linked
  private final AtomicReference<Boolean> filterOnlyActionArticle = new AtomicReference<>(false);

  public SpecialPriceEditorController() {
    super(new SpecialPriceEditorModel());
    this.searchBoxController =
        new SearchBoxController<Article>(
            this::search,
            new Column<Article>() {
              @Override
              public String getName() {
                return "Name";
              }

              @Override
              public Object getValue(Article article) throws PermissionKeyRequiredException {
                return article.getName();
              }

              @Override
              public void adjust(TableColumn column) {
                column.setWidth(500);
              }

              @Override
              public TableCellRenderer getRenderer() {
                return Column.DEFAULT_STRIPED_RENDERER;
              }
            },
            Column.create("Packungsmenge", Article::getAmount),
            Column.create("Lieferant", Article::getSupplier),
            Column.create("Lieferanten Nr.", Article::getSuppliersItemNumber));
    searchBoxController.addSelectionListener(this::load);
  }

  private Collection<Article> search(String s, int i) {
    if (!isInViewInitialize()) {
      load(null);
    }
    return model.searchArticle(s, i, filterOnlyActionArticle.get());
  }

  void load(Article article) {
    var view = getView();
    view.setSpecialNetPrice(0);
    if (article == null) {
      view.setOffers(CollectionUtils.EMPTY_COLLECTION);
      view.setAddEnable(false);
      view.setSelectedArticleIdentifier(null);
      view.setSelectedArticleNetPrice(0);
    } else {
      if (LocalDate.now().getDayOfMonth() >= 15) {
        view.setFrom(
            Instant.from(
                LocalDate.now()
                    .plusMonths(1)
                    .with(TemporalAdjusters.firstDayOfMonth())
                    .atStartOfDay(ZoneId.systemDefault())));
        view.setTo(
            Instant.from(
                    LocalDate.now()
                        .plusMonths(2)
                        .with(TemporalAdjusters.lastDayOfMonth())
                        .atStartOfDay(ZoneId.systemDefault()))
                .minusNanos(1));
      } else {
        view.setFrom(
            Instant.from(
                LocalDate.now()
                    .with(TemporalAdjusters.firstDayOfMonth())
                    .atStartOfDay(ZoneId.systemDefault())));
        view.setTo(
            Instant.from(
                    LocalDate.now()
                        .plusMonths(1)
                        .with(TemporalAdjusters.lastDayOfMonth())
                        .atStartOfDay(ZoneId.systemDefault()))
                .minusNanos(1));
      }
      view.setOffers(article.getAllOffers());
      view.setAddEnable(true);
      view.setSelectedArticleIdentifier(article.getName());
      view.setSelectedArticleNetPrice(article.getNetPrice());
    }
    view.setRemoveEnable(false);
    view.setEditEnable(false);
  }

  void selectOffer(Offer offer) {
    var view = getView();
    if (offer == null) {
      view.setFrom(Instant.from(LocalDate.now()));
      view.setTo(Instant.from(LocalDate.now()));
      view.setSpecialNetPrice(0.0);
      view.setRepeat(Repeat.EVERY_YEAR);
      view.setEditEnable(false);
      view.setRemoveEnable(false);
    }
    view.setFrom(offer.getFromDate());
    view.setTo(offer.getToDate());
    view.setSpecialNetPrice(offer.getSpecialNetPrice());
    view.setRepeat(offer.getRepeatMode());
    view.setEditEnable(true);
    view.setRemoveEnable(true);
  }

  public void add() {
    try {
      Offer o = collect();
      Article base = searchBoxController.getSelectedObject();
      if (o.getSpecialNetPrice() > base.getNetPrice() && !getView().commitStrangeNetPrice()) return;
      model.addOffer(base, collect());
      getView().setOffers(searchBoxController.getSelectedObject().getAllOffers());
    } catch (IncorrectInput incorrectInput) {
      getView().cannotParseDateFormat();
    }
  }

  @NotNull
  private Offer collect() throws IncorrectInput {
    Offer out = new Offer();
    out.setFromDate(getView().getFrom());
    out.setToDate(getView().getTo());
    out.setSpecialNetPrice(getView().getSpecialPrice());
    out.setRepeatMode(getView().getRepeatMode());
    return out;
  }

  public void edit() {
    try {
      getView().setOffers(searchBoxController.getSelectedObject().getAllOffers());
      model.edit(getView().getSelectedOffer().getId(), collect());
    } catch (IncorrectInput incorrectInput) {
      getView().cannotParseDateFormat();
    }
  }

  public void remove() {
    model.remove(getView().getSelectedOffer());
    getView().setOffers(searchBoxController.getSelectedObject().getAllOffers());
  }

  void searchFrom() {
    DatePickerController.requestDate(getView().traceViewContainer(), getView()::setFrom);
  }

  void searchTo() {
    DatePickerController.requestDate(getView().traceViewContainer(), getView()::setTo);
  }

  void invokeSearch() {
    searchBoxController.invokeSearch();
  }

  @Override
  public @NotNull SpecialPriceEditorModel getModel() {
    return model;
  }

  @Override
  public void fillView(SpecialPriceEditorView specialPriceEditorView) {
    getView().fillRepeat(Repeat.values());
    int year = LocalDate.now().getYear();
    getView()
        .setMonth(
            new YearMonth[] {
              YearMonth.of(year, 1),
              YearMonth.of(year, 2),
              YearMonth.of(year, 3),
              YearMonth.of(year, 4),
              YearMonth.of(year, 5),
              YearMonth.of(year, 6),
              YearMonth.of(year, 7),
              YearMonth.of(year, 8),
              YearMonth.of(year, 9),
              YearMonth.of(year, 10),
              YearMonth.of(year, 11),
              YearMonth.of(year, 12),
            });
  }



  public void loadMonth() {
    getView()
        .setOffersMonth(
            model.getAllOffersBetween(
                getView().getMonth().atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
                getView()
                    .getMonth()
                    .atEndOfMonth()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()));
  }
}
