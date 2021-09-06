package kernbeisser.Windows.CashierShoppingMask;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.*;
import java.util.Collection;
import java.util.Locale;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.PermissionButton;
import kernbeisser.CustomComponents.PermissionComboBox;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.User;
import kernbeisser.Exeptions.NoSelectionException;
import kernbeisser.Security.StaticMethodTransformer.StaticAccessPoint;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class CashierShoppingMaskView implements IView<CashierShoppingMaskController> {

  private JPanel main;
  private SearchBoxView<User> searchBoxView;
  private PermissionButton openShoppingMask;
  private PermissionComboBox<User> secondSellerUsername;
  private JButton userInfo;
  private JButton close;

  @Linked
  private CashierShoppingMaskController controller;

  @Linked
  private SearchBoxController<User> searchBoxController;

  private void createUIComponents() {
    searchBoxView = searchBoxController.getView();
  }

  public void setStartFor(String firstName, String surname) {
    openShoppingMask.setIcon(
        IconFontSwing.buildIcon(FontAwesome.SHOPPING_CART, 20, new Color(49, 114, 128)));
    openShoppingMask.setText("Einkauf für " + surname + ", " + firstName + " beginnen");
  }

  public User getSecondSeller() {
    return (User) secondSellerUsername.getSelectedItem();
  }

  public void setOpenShoppingMaskEnabled(boolean b) {
    openShoppingMask.setEnabled(b);
  }

  public void setUserInfoEnabled(boolean b) {
    userInfo.setEnabled(b);
  }

  @Override
  public void initialize(CashierShoppingMaskController controller) {
    openShoppingMask.addActionListener(e -> controller.openMaskWindow());
    userInfo.setIcon(IconFontSwing.buildIcon(FontAwesome.INFO, 20, new Color(49, 114, 128)));
    userInfo.addActionListener(
        e -> {
          try {
            controller.openUserInfo();
          } catch (NoSelectionException noSelectionException) {
            messageSelectUserFirst();
          }
        });
    close.addActionListener(e -> back());
  }

  @Override
  public @NotNull Dimension getSize() {
    return new Dimension(1440, 950);
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  @Override
  @StaticAccessPoint
  public IconCode getTabIcon() {
    return FontAwesome.SHOPPING_CART;
  }

  public void setAllSecondarySellers(Collection<User> users) {
    secondSellerUsername.removeAllItems();
    secondSellerUsername.addItem(new User("Keiner"));
    users.forEach(e -> secondSellerUsername.addItem(e));
  }

  public void notEnoughCredit() {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Der Ausgewählte Benutzer hat nicht genug Guthaben, um einen Einkauf zu beginnen!");
  }

  @Override
  public String getTitle() {
    return "Ladendienst-Menü";
  }

  public boolean commitClose() {
    return JOptionPane.showConfirmDialog(
        getTopComponent(),
        "Bist du dir sicher das du den Ladendienst beenden\n"
            + "und den Ladendienst-Report ausdrucken möchtest?",
        "Ladendienst beenden",
        JOptionPane.YES_NO_OPTION)
        == 0;
  }

  public void messageSelectUserFirst() {
    message("Bitte wähle zunächst ein Nutzer aus.");
  }

  public void messageDontPanic() {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Der Ausdruck ist fehlgeschlagen! Das ist nicht so schlimm,\n"
            + "die Bons von heute erscheinen dann erst beim nächsten erfolgreichen Ausdruck!");
  }

  public void messageDoPanic(long no) {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Der Ausdruck ist fehlgeschlagen! Da jetzt schon seit "
            + no
            + " Bons kein \n"
            + "Bericht erstellt wurde, sollte die It-Gruppe informiert werden.");
  }

  public void messageShoppingMaskAlreadyOpened() {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Es ist bereits ein Einkaufs-Fenster geöffnet, um mehrere Einkaufsfenster öffnen zu können, aktiviere dies bitte explizit in deinen Einstellungen.");
  }

}
