package kernbeisser.Windows.SpecialPriceEditor;

import kernbeisser.CustomComponents.DatePicker.DatePickerController;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Offer;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Repeat;
import kernbeisser.Exeptions.IncorrectInput;
import kernbeisser.Main;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Window;
import kernbeisser.Windows.WindowImpl.JFrameWindow;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class SpecialPriceEditorController implements Controller<SpecialPriceEditorView,SpecialPriceEditorModel> {
    public static void main(String[] args)
            throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException,
                   IllegalAccessException {
        Main.buildEnvironment();
        new SpecialPriceEditorController(null).openTab("IDK");
    }

    private SpecialPriceEditorView view;
    private final SpecialPriceEditorModel model;

    private final SearchBoxController<Article> searchBoxController;

    SpecialPriceEditorController(Window current) {
        this.model = new SpecialPriceEditorModel();
        this.searchBoxController = new SearchBoxController<Article>(
                (s, m) -> model.searchArticle(s, m, view != null && view.filterOnlyActionArticle()),
                Column.create("Name", Article::getName),
                Column.create("Packungsmenge", Article::getAmount),
                Column.create("Lieferant",
                              Article::getSupplier),
                Column.create("Lieferanten Nr.", Article::getSuppliersItemNumber),
                Column.create("Kernbeissernummer",
                              Article::getKbNumber)
        ) {
            @Override
            public void search() {
                super.search();
                if (view != null) {
                    load(null);
                }
            }
        };
        searchBoxController.initView();
        this.view = new SpecialPriceEditorView(this);
        searchBoxController.addSelectionListener(this::load);
    }

    void load(Article article) {
        if (article == null) {
            view.setOffers(CollectionUtils.EMPTY_COLLECTION);
            view.setAddEnable(false);
            view.setSelectedArticleIdentifier(null);
            view.setSelectedArticleNetPrice(0);
        } else {
            view.setOffers(article.getOffers());
            view.setAddEnable(true);
            view.setSelectedArticleIdentifier(article.getName());
            view.setSelectedArticleNetPrice(article.getNetPrice());
        }
        model.setSelectedArticle(article);
        view.setRemoveEnable(false);
        view.setEditEnable(false);
    }

    void selectOffer() {
        Offer o = view.getSelectedOffer();
        model.setSelectedOffer(o);
        view.setFrom(o.getFromDate().toLocalDate());
        view.setTo(o.getToDate().toLocalDate());
        view.setSpecialNetPrice(o.getSpecialNetPrice());
        view.setRepeat(o.getRepeatMode());
        view.setEditEnable(true);
        view.setRemoveEnable(true);
    }


    public void add() {
        try {
            model.addOffer(model.getSelectedArticle(), collect());
            model.refreshItem();
            view.setOffers(model.getSelectedArticle().getOffers());
        } catch (IncorrectInput incorrectInput) {
            view.cannotParseDateFormat();
        }
    }

    @NotNull
    private Offer collect() throws IncorrectInput {
        Offer out = new Offer();
        out.setFromDate(view.getFrom());
        out.setToDate(view.getTo());
        out.setSpecialNetPrice(view.getSpecialPrice());
        out.setRepeatMode(view.getRepeatMode());
        return out;
    }

    public void edit() {
        try {
            model.refreshItem();
            view.setOffers(model.getSelectedArticle().getOffers());
            model.edit(model.getSelectedOffer().getOid(), collect());
        } catch (IncorrectInput incorrectInput) {
            view.cannotParseDateFormat();
        }
    }

    public void remove() {
        model.remove(model.getSelectedArticle(), model.getSelectedOffer());
        model.refreshItem();
        view.setOffers(model.getSelectedArticle().getOffers());
    }

    void searchFrom() {
        DatePickerController.requestDate((JFrameWindow) view.getWindow(), view::setFrom);
    }

    SearchBoxView<Article> getSearchBoxView() {
        return searchBoxController.getView();
    }

    void searchTo() {
        DatePickerController.requestDate((JFrameWindow) view.getWindow(), view::setTo);
    }

    void refreshSearchSolutions() {
        searchBoxController.refreshLoadSolutions();
    }

    @Override
    public @NotNull SpecialPriceEditorView getView() {
        return view;
    }

    @Override
    public @NotNull SpecialPriceEditorModel getModel() {
        return model;
    }

    @Override
    public void fillUI() {
        view.fillRepeat(Repeat.values());
    }

    @Override
    public PermissionKey[] getRequiredKeys() {
        return new PermissionKey[0];
    }
}
