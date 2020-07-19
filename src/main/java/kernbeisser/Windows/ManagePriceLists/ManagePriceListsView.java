/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kernbeisser.Windows.ManagePriceLists;

import kernbeisser.CustomComponents.PermissionButton;
import kernbeisser.CustomComponents.PriceListTree;
import kernbeisser.CustomComponents.TextFields.PermissionField;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ManagePriceListsView implements View<ManagePriceListsController> {

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

    private final ManagePriceListsController controller;

    public ManagePriceListsView(ManagePriceListsController controller) {
        this.controller = controller;
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

    @Override
    public void initialize(ManagePriceListsController controller) {
        add.setRequiredWriteKeys(PermissionKey.ACTION_ADD_PRICELIST, PermissionKey.PRICE_LIST_NAME_WRITE);
        delete.setRequiredWriteKeys(PermissionKey.ACTION_DELETE_PRICELIST);
        edit.setRequiredWriteKeys(PermissionKey.ACTION_EDIT_PRICELIST, PermissionKey.PRICE_LIST_NAME_WRITE,
                                  PermissionKey.PRICE_LIST_SUPER_PRICE_LIST_WRITE);
        priceListName.setRequiredWriteKeys(PermissionKey.PRICE_LIST_NAME_WRITE);
        priceListTree.addSelectionListener(e -> controller.displayCurrentSuperPriceList());
        add.addActionListener(e -> controller.saveAction());
        edit.addActionListener(e -> controller.renameAction());
        delete.addActionListener(e -> controller.deleteAction());
        back.addActionListener(e -> controller.back());
        commit.addActionListener((e -> controller.back()));
    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }

}
