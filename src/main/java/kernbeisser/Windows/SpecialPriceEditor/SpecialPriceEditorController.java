package kernbeisser.Windows.SpecialPriceEditor;

import kernbeisser.CustomComponents.DatePicker.DatePickerController;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Offer;
import kernbeisser.Enums.Repeat;
import kernbeisser.Main;
import kernbeisser.Windows.Window;

import javax.swing.*;

public class SpecialPriceEditorController {
    public static void main(String[] args)
            throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException,
                   IllegalAccessException {
        Main.buildEnvironment();
        new SpecialPriceEditorController(null);
    }
    private final SpecialPriceEditorView view;
    private final SpecialPriceEditorModel model;
    SpecialPriceEditorController(Window current){
        this.view = new SpecialPriceEditorView(this,current);
        this.model = new SpecialPriceEditorModel();
        view.fillRepeat(Repeat.values());
    }

    void load(Article article) {
        model.setSelectedArticle(article);
        view.setOffers(article.getSpecialPriceMonths());
        view.setRemoveEnable(false);
        view.setEditEnable(false);
    }

    void selectOffer(){
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
        model.addOffer(model.getSelectedArticle(), collect());
        model.refreshItem();
        view.setOffers(model.getSelectedArticle().getSpecialPriceMonths());
    }

    private Offer collect(){
        Offer out = new Offer();
        out.setFromDate(view.getFrom());
        out.setToDate(view.getTo());
        out.setSpecialNetPrice(view.getSpecialPrice());
        out.setRepeatMode(view.getRepeatMode());
        return out;
    }

    public void edit() {
        model.refreshItem();
        view.setOffers(model.getSelectedArticle().getSpecialPriceMonths());
        model.edit(model.getSelectedOffer().getOid(),collect());
    }

    public void remove() {
        model.remove(model.getSelectedArticle(), model.getSelectedOffer());
        model.refreshItem();
        view.setOffers(model.getSelectedArticle().getSpecialPriceMonths());
    }

    void searchFrom() {

        DatePickerController.requestDate(view,view::setFrom);
    }

    void searchTo() {
        DatePickerController.requestDate(view,view::setTo);
    }
}
