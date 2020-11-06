package kernbeisser.Windows.SpecialPriceEditor;

import java.time.LocalDate;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import kernbeisser.CustomComponents.DatePicker.DatePickerController;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Offer;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Repeat;
import kernbeisser.Exeptions.IncorrectInput;
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
            Column.create("Name", Article::getName),
            Column.create("Packungsmenge", Article::getAmount),
            Column.create("Lieferant", Article::getSupplier),
            Column.create("Lieferanten Nr.", Article::getSuppliersItemNumber),
            Column.create("Kernbeissernummer", Article::getKbNumber));
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
    if (article == null) {
      view.setOffers(CollectionUtils.EMPTY_COLLECTION);
      view.setAddEnable(false);
      view.setSelectedArticleIdentifier(null);
      view.setSelectedArticleNetPrice(0);
    } else {
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
      view.setFrom(LocalDate.now());
      view.setTo(LocalDate.now());
      view.setSpecialNetPrice(0.0);
      view.setRepeat(Repeat.EVERY_YEAR);
      view.setEditEnable(false);
      view.setRemoveEnable(false);
    }
    view.setFrom(offer.getFromDate().toLocalDate());
    view.setTo(offer.getToDate().toLocalDate());
    view.setSpecialNetPrice(offer.getSpecialNetPrice());
    view.setRepeat(offer.getRepeatMode());
    view.setEditEnable(true);
    view.setRemoveEnable(true);
  }

  public void add() {
    try {
      model.addOffer(searchBoxController.getSelectedObject(), collect());
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

  SearchBoxView<Article> getSearchBoxView() {
    return searchBoxController.getView();
  }

  void searchTo() {
    DatePickerController.requestDate(getView().traceViewContainer(), getView()::setTo);
  }

  void refreshSearchSolutions() {
    searchBoxController.refreshLoadSolutions();
  }

  @Override
  public @NotNull SpecialPriceEditorModel getModel() {
    return model;
  }

  @Override
  public void fillView(SpecialPriceEditorView specialPriceEditorView) {
    getView().fillRepeat(Repeat.values());
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }
}
