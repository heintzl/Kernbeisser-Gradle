package kernbeisser.Windows.ManagePriceLists;

import kernbeisser.CustomComponents.PriceListTree;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;

public class ManagePriceListsController implements Controller {
    private ManagePriceListsModel model;
    private ManagePriceListsView view;

    /*ManagePriceListsController(ManagePriceListsView view) {
        this.view = view;
        this.model = new ManagePriceListsModel();
    }
    */
    public ManagePriceListsController(Window current) {
        model = new ManagePriceListsModel();
        view = new ManagePriceListsView(this, current);
        refresh();
    }

    void displayCurrentSuperPriceList() {
        if (view.getPriceListChooser().getSelectionPath() != null) {
            if (view.getPriceListChooser().getSelectionPath().getPath().length > 1) {
                view.getSuperPriceList().setText(view.getPriceListChooser().getLastSelectedPathComponent().toString());
            }
        }
    }

    void saveAction(){
        String priceListName = view.newPriceListName();
        if (priceListName.equals("")) {
            JOptionPane.showMessageDialog(view, "Bitte w\u00e4hlen sie einen korrekten Namen");
            return;
        }
        model.savePriceList(priceListName, view.selectedSuperPriceList());
        refresh();
        return;
    }

    void renameAction() {

    }

    void deleteAction() {

    }

    @Override
    public void refresh() {
        model.refresh();
        view.getPriceListChooser().setModel(model.getPriceListTreeModel());
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public Model getModel() {
        return model;
    }
}
