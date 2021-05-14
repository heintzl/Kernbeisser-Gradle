package kernbeisser.Windows.ArticleOffersEditor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Offer;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Repeat;
import kernbeisser.Exeptions.NoSelectionException;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Forms.ObjectForm.Components.Source;
import kernbeisser.Forms.ObjectForm.Exceptions.CannotParseException;
import kernbeisser.Forms.ObjectForm.Exceptions.SilentParseException;
import kernbeisser.Reports.PriceListReport;
import kernbeisser.Reports.Report;
import kernbeisser.Security.Key;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;
import lombok.var;
import org.jetbrains.annotations.NotNull;

public class ArticleOffersEditorController
    extends Controller<ArticleOffersEditorView, ArticleOffersEditorModel> {

  @Linked private final SearchBoxController<Article> searchBoxController;

  @Linked
  private final AtomicReference<Boolean> filterOnlyActionArticle = new AtomicReference<>(false);

  @Key(PermissionKey.ACTION_OPEN_SPECIAL_PRICE_EDITOR)
  public ArticleOffersEditorController() {
    super(new ArticleOffersEditorModel());
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
  }

  private Collection<Article> search(String s, int i) {
    if (!isInViewInitialize()) {
      load(null);
    }
    return model.searchArticle(s, i, filterOnlyActionArticle.get());
  }

  private Offer prepareDefaultOffer(Article article) {
    final Offer offer = new Offer();
    offer.setArticle(article);
    offer.setFromDate(defaultFromDate());
    offer.setToDate(defaultToDate());
    offer.setSpecialNetPrice(article.getNetPrice());
    offer.setRepeatMode(Repeat.NONE);
    return offer;
  }

  public Instant defaultFromDate() {
    return LocalDate.now().getDayOfMonth() >= 15
        ? Instant.from(
                LocalDate.now()
                    .plusMonths(2)
                    .with(TemporalAdjusters.lastDayOfMonth())
                    .atStartOfDay(ZoneId.systemDefault()))
            .minusNanos(1)
        : Instant.from(
            LocalDate.now()
                .with(TemporalAdjusters.firstDayOfMonth())
                .atStartOfDay(ZoneId.systemDefault()));
  }

  private Instant defaultToDate() {
    return LocalDate.now().getDayOfMonth() >= 15
        ? Instant.from(
                LocalDate.now()
                    .plusMonths(2)
                    .with(TemporalAdjusters.firstDayOfMonth())
                    .atStartOfDay(ZoneId.systemDefault()))
            .minusNanos(1)
        : Instant.from(
                LocalDate.now()
                    .plusMonths(1)
                    .with(TemporalAdjusters.firstDayOfMonth())
                    .atStartOfDay(ZoneId.systemDefault()))
            .minusNanos(1);
  }

  void load(Article article) {
    var view = getView();
    view.setSpecialNetPrice(0);
    if (article == null) {
      view.setOffers(Collections.emptyList());
      view.setAddEnable(false);
      view.setSelectedArticleIdentifier(null);
      view.setSpecialNetPrice(0.0);
    } else {
      view.setOffers(article.getAllOffers());
      view.setAddEnable(true);
      view.setSelectedArticleIdentifier(article.getName());
      view.getObjectForm().setSource(prepareDefaultOffer(article));
    }
    view.setModifyEnable(false);
  }

  public void add() {
    if (getView().getObjectForm().applyMode(Mode.ADD)) {
      getView()
          .setOffers(
              searchBoxController
                  .getSelectedObject()
                  .map(Article::getAllOffers)
                  .orElse(Collections.emptyList()));
      loadMonth();
    }
  }

  private Offer getSelectedOffer() throws NoSelectionException {
    try {
      return getView().getSelectedOffer().orElseThrow(NoSelectionException::new);
    } catch (NoSelectionException e) {
      getView().messageSelectOffer();
      throw e;
    }
  }

  public void edit() {
    if (getView().getObjectForm().applyMode(Mode.EDIT)) {
      getView()
          .setOffers(
              searchBoxController
                  .getSelectedObject()
                  .map(Article::getAllOffers)
                  .orElse(Collections.emptyList()));
      loadMonth();
    }
  }

  public void printMonth() {
    ArrayList<Article> articles =
        getAllOffersInSelectedMonth().stream()
            .map(Offer::getArticle)
            .collect(Collectors.toCollection(ArrayList::new));
    PriceList fakeInstance =
        new PriceList() {
          @Override
          public List<Article> getAllArticles() {
            return articles;
          }
        };
    fakeInstance.setName("Aktionsartikel: " + getView().getMonth());
    PriceListReport report = new PriceListReport(fakeInstance);
    report.sendToPrinter("Preisliste wird gedruckt...", Report::pdfExportException);
  }

  public void remove() {
    try {
      model.remove(getView().getObjectForm().getOriginal());
      loadMonth();
      getView()
          .setOffers(
              searchBoxController
                  .getSelectedObject()
                  .orElseThrow(NoSelectionException::new)
                  .getAllOffers());
    } catch (NoSelectionException e) {
      e.printStackTrace();
    }
  }

  void invokeSearch() {
    searchBoxController.invokeSearch();
  }

  @Override
  public @NotNull ArticleOffersEditorModel getModel() {
    return model;
  }

  @Override
  public void fillView(ArticleOffersEditorView specialPriceEditorView) {
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

  public Collection<Offer> getAllOffersInSelectedMonth() {
    return model.getAllOffersBetween(
        getView().getMonth().atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
        getView().getMonth().atEndOfMonth().atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  public void loadMonth() {
    getView().setOffersMonth(getAllOffersInSelectedMonth());
  }

  public void lostContact() {
    load(null);
  }

  public void validateOffer(Offer offer, Mode mode) throws CannotParseException {
    if (offer.getArticle() == null) {
      getView().messageSelectArticle();
      throw new SilentParseException();
    }
    if (offer.getFromDate().isAfter(offer.getToDate())) {
      getView().messageInvalidTimeSpace();
      throw new SilentParseException();
    }
    if (offer.getSpecialNetPrice() > offer.getArticle().getNetPrice()
        && !getView().commitStrangeNetPrice()) {
      throw new SilentParseException();
    }
  }

  public void loadOffer(Offer offer) {
    load(offer.getArticle());
    getView().getObjectForm().setSource(offer);
    getView().setModifyEnable(true);
  }

  public Source<Repeat> getRepeatModes() {
    return () -> Arrays.asList(Repeat.values());
  }
}
