/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kernbeisser.Windows.ManagePriceLists;

import kernbeisser.CustomComponents.PermissionButton;
import kernbeisser.CustomComponents.PriceListTree;
import kernbeisser.CustomComponents.TextFields.PermissionField;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.Enums.Key;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.swing.*;

public class ManagePriceListsView extends Window implements View {

    //TODO back or commit button can be deleted, they should have the same function
    private PermissionField priceListName;
    private PriceListTree priceListTree;
    private kernbeisser.CustomComponents.PermissionButton add;
    private kernbeisser.CustomComponents.PermissionButton edit;
    private JButton back;
    private JButton commit;
    private PermissionField superPriceListName;
    private JPanel main;
    private PermissionButton delete;

    private ManagePriceListsController controller;

    public ManagePriceListsView(Window current, ManagePriceListsController controller) {
        //TODO Benjamin is working currently on this project
        super(current /*,Key.ACTION_OPEN_MANAGE_PRICELISTS*/);
        this.controller = controller;
        add.setRequiredWriteKeys(Key.ACTION_ADD_PRICELIST, Key.PRICELIST_NAME_WRITE);
        delete.setRequiredWriteKeys(Key.ACTION_DELETE_PRICELIST);
        edit.setRequiredWriteKeys(Key.ACTION_EDIT_PRICELIST, Key.PRICELIST_NAME_WRITE,
                                  Key.PRICELIST_SUPER_PRICE_LIST_WRITE);
        priceListName.setRequiredWriteKeys(Key.PRICELIST_NAME_WRITE);
        priceListTree.addSelectionListener(e -> controller.displayCurrentSuperPriceList());
        add(main);
        add.addActionListener(e -> controller.saveAction());
        edit.addActionListener(e -> controller.renameAction());
        delete.addActionListener(e -> controller.deleteAction());
        back.addActionListener(e -> controller.back());
        commit.addActionListener((e -> controller.back()));
        windowInitialized();
    }

    PriceList getSelectedPriceList() {
        return priceListTree.getSelected();
    }

    String getPriceListName() {
        return priceListName.getText();
    }

    //If you want to support SuperPriceList editing
    String getSuperPriceListName() {
        return superPriceListName.getText();
    }

    //If you want to support SuperPriceList editing
    void setSuperPriceListNameEnable(boolean b) {
        superPriceListName.setEnabled(b);
    }

    void setPriceListName(String s) {
        priceListName.setText(s);
    }

    void setSuperPriceListName(String s) {
        superPriceListName.setText(s);
    }


    private void createUIComponents() {
        priceListTree = new PriceListTree();
    }

    public PriceListTree getPriceListTree() {
        return priceListTree;
    }

}