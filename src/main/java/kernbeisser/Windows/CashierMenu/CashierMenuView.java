package kernbeisser.Windows.CashierMenu;

import kernbeisser.Windows.Controller;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;

public class CashierMenuView extends Window implements View {
    private JButton editItems;
    private JButton editUser;
    private JButton changeSurchargeTable;
    private JButton editPriceList;
    private JButton quitCashierJob;
    private JButton startCashier;
    private JButton refreshCatalog;
    private JPanel main;

    CashierMenuView(CashierMenuController controller,Window current){
        super(current);
        editItems.addActionListener(e -> controller.openManageItems());
        editUser.addActionListener(e -> controller.openManageUsers());
        editPriceList.addActionListener(e -> controller.openManagePriceLists());
        refreshCatalog.addActionListener(e -> controller.openCatalogInput());
        startCashier.addActionListener(e -> controller.openCashierMask());
        quitCashierJob.addActionListener(e -> back());
        changeSurchargeTable.addActionListener(e -> controller.openManageSurchargeTables());
        add(main);
        setSize(900,600);
        setLocationRelativeTo(null);
    }

}
