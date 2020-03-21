package kernbeisser.Windows.CashierMenu;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class CashierMenuView extends Window implements View {
    private JButton editItems;
    private JButton editUser;
    private JButton changeSurchargeTable;
    private JButton editPriceList;
    private JButton quitCashierJob;
    private JButton startCashier;
    private JButton refreshCatalog;
    private JPanel main;
    private JButton transfer;

    CashierMenuView(CashierMenuController controller, Window current) {
        super(current);
        editItems.addActionListener(e -> controller.openManageItems());
        editUser.addActionListener(e -> controller.openManageUsers());
        editPriceList.addActionListener(e -> controller.openManagePriceLists());
        refreshCatalog.addActionListener(e -> controller.openCatalogInput());
        startCashier.addActionListener(e -> controller.openCashierMask());
        quitCashierJob.addActionListener(e -> back());
        transfer.addActionListener(e -> controller.openTransfer());
        startCashier.setIcon(IconFontSwing.buildIcon(FontAwesome.SHOPPING_CART, 20, new Color(49, 114, 128)));
        startCashier.setRolloverIcon(IconFontSwing.buildIcon(FontAwesome.SHOPPING_CART, 25, new Color(43, 128, 9)));
        editUser.setIcon(IconFontSwing.buildIcon(FontAwesome.PENCIL, 20, new Color(165, 4, 46)));
        editPriceList.setIcon(IconFontSwing.buildIcon(FontAwesome.PENCIL, 20, new Color(165, 4, 46)));
        editItems.setIcon(IconFontSwing.buildIcon(FontAwesome.PENCIL, 20, new Color(165, 4, 46)));
        changeSurchargeTable.setIcon(IconFontSwing.buildIcon(FontAwesome.PENCIL, 20, new Color(165, 4, 46)));
        refreshCatalog.setIcon(IconFontSwing.buildIcon(FontAwesome.REFRESH, 20, new Color(60, 57, 255)));
        changeSurchargeTable.addActionListener(e -> controller.openManageSurchargeTables());
        add(main);
        setSize(900, 600);
        setLocationRelativeTo(null);
        windowInitialized();
    }

}
