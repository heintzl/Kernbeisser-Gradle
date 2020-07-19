package kernbeisser.Windows.CashierMenu;

import java.awt.*;
import javax.swing.*;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

public class CashierMenuView implements View<CashierMenuController> {
  private JButton editItems;
  private JButton editUser;
  private JButton changeSurchargeTable;
  private JButton editPriceList;
  private JButton quitCashierJob;
  private JButton startCashier;
  private JPanel main;
  private JButton transfer;

  private final CashierMenuController cashierMenuController;

  CashierMenuView(CashierMenuController controller) {
    this.cashierMenuController = controller;
  }

  @Override
  public void initialize(CashierMenuController controller) {
    editItems.addActionListener(e -> controller.openManageItems());
    editUser.addActionListener(e -> controller.openManageUsers());
    editPriceList.addActionListener(e -> controller.openManagePriceLists());
    startCashier.addActionListener(e -> controller.openCashierMask());
    quitCashierJob.addActionListener(e -> back());
    transfer.addActionListener(e -> controller.openTransfer());
    startCashier.setIcon(
        IconFontSwing.buildIcon(FontAwesome.SHOPPING_CART, 20, new Color(49, 114, 128)));
    startCashier.setRolloverIcon(
        IconFontSwing.buildIcon(FontAwesome.SHOPPING_CART, 25, new Color(43, 128, 9)));
    editUser.setIcon(IconFontSwing.buildIcon(FontAwesome.PENCIL, 20, new Color(165, 4, 46)));
    editPriceList.setIcon(IconFontSwing.buildIcon(FontAwesome.PENCIL, 20, new Color(165, 4, 46)));
    editItems.setIcon(IconFontSwing.buildIcon(FontAwesome.PENCIL, 20, new Color(165, 4, 46)));
    changeSurchargeTable.setIcon(
        IconFontSwing.buildIcon(FontAwesome.PENCIL, 20, new Color(165, 4, 46)));
    changeSurchargeTable.addActionListener(e -> controller.openManageSurchargeTables());
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  @Override
  public @NotNull Dimension getSize() {
    return new Dimension(900, 600);
  }
}
