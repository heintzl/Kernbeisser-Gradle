package kernbeisser.Windows.ManagePriceLists;

import kernbeisser.CustomComponents.PriceListTree;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.Window;

import javax.persistence.PersistenceException;
import javax.swing.*;

public class ManagePriceListsController implements Controller {
    private ManagePriceListsModel model;
    private ManagePriceListsView view;

    //TODO When the root is selected, the displayCurrentSuperpriceList should be activated, too.
    //TODO Nice to have: At Changes in the Tree, not to reload the hole tree to keep expansion state und selected Node


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
        view.setSuperPriceListName(view.getSelectedPriceList().getName());
    }

    void saveAction(){
        String priceListName = view.getPriceListName();
        if (priceListName.equals("")) {
            JOptionPane.showMessageDialog(view, "Bitte w\u00e4hlen sie einen korrekten Namen");
            return;
        }
        model.savePriceList(priceListName, view.getSelectedPriceList());
        refresh();
        return;
    }

    void renameAction() {
        PriceList toRename = view.getSelectedPriceList();
        String newName = view.getPriceListName();
        if (toRename == null) {
            return;
        }
        if (newName.equals("")) {
            JOptionPane.showMessageDialog(view, "Bitte w\u00e4hlen sie einen korrekten Namen");
            return;
        }
        model.renamePriceList(toRename, newName);
        refresh();
    }

    void deleteAction() {
        PriceList toDelete = view.getSelectedPriceList();
        if (toDelete == null) {
            return;
        }
        if (JOptionPane.showConfirmDialog(view,
                                          "Soll die Preisliste " + toDelete.getName() + " wirklich gel\u00f6scht werden") == 0) {
            try {
                model.deletePriceList(toDelete);
                refresh();
            } catch (PersistenceException e) {
                JOptionPane.showMessageDialog(view, "Preisliste konnte nicht gelöscht werden.\n Entweder hat diese Preisliste noch Unterpreislisten oder Artikel, die auf ihr stehen.");
            }
        }
    }

    @Override
    public void refresh() {
        model.refresh();
        view.getPriceListTree().setModel(model.getPriceListTreeModel());
        view.setSuperPriceListName("");
        view.setPriceListName("");
    }

    //Only to override
    public void finish(){}

    public void back() {
        view.back();
    };

    @Override
    public ManagePriceListsView getView() {
        return view;
    }

    @Override
    public Model getModel() {
        return model;
    }
}
