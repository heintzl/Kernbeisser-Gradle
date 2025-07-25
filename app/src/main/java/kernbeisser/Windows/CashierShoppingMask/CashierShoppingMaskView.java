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
  private JButton editPost;

  @Linked private CashierShoppingMaskController controller;

  @Linked private SearchBoxController<User> searchBoxController;

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
    close.addActionListener(e -> controller.close());
    editPost.addActionListener(e -> controller.openPostOnClose());
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
            "Bist du dir sicher, dass du den Ladendienst beenden möchtest?",
            "Ladendienst beenden",
            JOptionPane.YES_NO_OPTION)
        == 0;
  }

  public void setEditPostVisible(boolean visible) {
    editPost.setVisible(visible);
  }

  public void messageSelectUserFirst() {
    message("Bitte wähle zunächst ein Nutzer aus.");
  }

  public void messageDontPanic() {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Der Ausdruck ist fehlgeschlagen! Das ist nicht so schlimm,\n"
            + "die Umsätze von heute erscheinen dann erst beim nächsten erfolgreichen Ausdruck!");
  }

  public void messageDoPanic(long no) {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Der Ausdruck ist fehlgeschlagen! Da jetzt schon seit "
            + no
            + " Umsätzen kein \n"
            + "Bericht erstellt wurde, sollte die It-Gruppe informiert werden.");
  }

  public void messageShoppingMaskAlreadyOpened() {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Es ist bereits ein Einkaufs-Fenster geöffnet, um mehrere Einkaufsfenster öffnen zu können, aktiviere dies bitte explizit in deinen Einstellungen.");
  }

  // @spotless:off

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /** Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        main = new JPanel();
        main.setLayout(new GridLayoutManager(1, 1, new Insets(0, 5, 5, 5), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 1, new Insets(2, 0, 2, 0), -1, -1));
        main.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 7, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        secondSellerUsername = new PermissionComboBox();
        panel2.add(secondSellerUsername, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Zusatzladendienst");
        panel2.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        close = new JButton();
        close.setText("Schließen");
        panel2.add(close, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 35), null, null, 0, false));
        openShoppingMask = new PermissionButton();
        openShoppingMask.setActionCommand("open");
        openShoppingMask.setEnabled(false);
        Font openShoppingMaskFont = this.$$$getFont$$$(null, Font.BOLD, -1, openShoppingMask.getFont());
        if (openShoppingMaskFont != null) openShoppingMask.setFont(openShoppingMaskFont);
        openShoppingMask.setLabel("Einkäufer*in in der Liste auswählen oder doppelklicken");
        openShoppingMask.setText("Einkäufer*in in der Liste auswählen oder doppelklicken");
        panel2.add(openShoppingMask, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 35), null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        editPost = new JButton();
        editPost.setText("Popup bearbeiten");
        panel2.add(editPost, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 35), null, null, 0, false));
        userInfo = new JButton();
        userInfo.setEnabled(false);
        userInfo.setText("Benutzerinfo");
        panel2.add(userInfo, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 35), null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        panel1.add(separator1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel1.add(searchBoxView.$$$getRootComponent$$$(), new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /** @noinspection ALL */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /** @noinspection ALL */
    public JComponent $$$getRootComponent$$$() {
        return main;
    }

    // @spotless:on
}
