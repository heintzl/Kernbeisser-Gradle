package kernbeisser.Windows.ManagePriceLists;

import kernbeisser.CustomComponents.PriceListTree;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.Window;

public class ManagePriceListsController implements Controller {
    private ManagePriceListsModel model;
    private ManagePriceListsView view;


    public ManagePriceListsController(Window current) {
        this.view = new ManagePriceListsView(current,this){
            @Override
            public void finish() {
                ManagePriceListsController.this.finish();
            }
        };
        model = new ManagePriceListsModel();
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

    //Only to override
    public void finish(){}

    @Override
    public ManagePriceListsView getView() {
        return view;
    }

    @Override
    public Model getModel() {
        return model;
    }
}
