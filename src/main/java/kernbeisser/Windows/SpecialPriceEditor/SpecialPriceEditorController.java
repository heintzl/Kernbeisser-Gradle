package kernbeisser.Windows.SpecialPriceEditor;

import javax.swing.*;
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
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

public class SpecialPriceEditorController
    extends Controller<SpecialPriceEditorView, SpecialPriceEditorModel> {

  @Linked private final SearchBoxController<Article> searchBoxController;

  SpecialPriceEditorController() {
    super(new SpecialPriceEditorModel());
    this.searchBoxController =
        new SearchBoxController<Article>(
            (s, m) ->
                model.searchArticle(s, m, getView() != null && getView().filterOnlyActionArticle()),
            Column.create("Name", Article::getName),
            Column.create("Packungsmenge", Article::getAmount),
            Column.create("Lieferant", Article::getSupplier),
            Column.create("Lieferanten Nr.", Article::getSuppliersItemNumber),
            Column.create("Kernbeissernummer", Article::getKbNumber)) {
          @Override
          public void search() {
            super.search();
            if (getView() != null) {
              load(null);
            }
          }
        };
    searchBoxController.addSelectionListener(this::load);
  }

  void load(Article article) {
    if (article == null) {
      getView().setOffers(CollectionUtils.EMPTY_COLLECTION);
      getView().setAddEnable(false);
      getView().setSelectedArticleIdentifier(null);
      getView().setSelectedArticleNetPrice(0);
    } else {
      getView().setOffers(article.getOffers());
      getView().setAddEnable(true);
      getView().setSelectedArticleIdentifier(article.getName());
      getView().setSelectedArticleNetPrice(article.getNetPrice());
    }
    model.setSelectedArticle(article);
    getView().setRemoveEnable(false);
    getView().setEditEnable(false);
  }

  void selectOffer() {
    Offer o = getView().getSelectedOffer();
    model.setSelectedOffer(o);
    getView().setFrom(o.getFromDate().toLocalDate());
    getView().setTo(o.getToDate().toLocalDate());
    getView().setSpecialNetPrice(o.getSpecialNetPrice());
    getView().setRepeat(o.getRepeatMode());
    getView().setEditEnable(true);
    getView().setRemoveEnable(true);
  }

  public void add() {
    try {
      model.addOffer(model.getSelectedArticle(), collect());
      model.refreshItem();
      getView().setOffers(model.getSelectedArticle().getOffers());
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
      model.refreshItem();
      getView().setOffers(model.getSelectedArticle().getOffers());
      model.edit(model.getSelectedOffer().getId(), collect());
    } catch (IncorrectInput incorrectInput) {
      getView().cannotParseDateFormat();
    }
  }

  public void remove() {
    model.remove(model.getSelectedArticle(), model.getSelectedOffer());
    model.refreshItem();
    getView().setOffers(model.getSelectedArticle().getOffers());
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
