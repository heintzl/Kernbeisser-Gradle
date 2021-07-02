package kernbeisser.Forms.ObjectView;

import java.awt.*;
import javax.swing.*;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.Security.StaticMethodTransformer.StaticAccessPoint;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class ObjectViewView<T> implements IView<ObjectViewController<T>> {
  private JButton add;
  private JButton edit;
  private JButton back;
  private JButton delete;
  private SearchBoxView<T> searchBoxView;
  private JPanel main;
  private JPanel extraButtons;

  @Linked private ObjectViewController<T> controller;

  @Linked private SearchBoxController<T> searchBoxController;

  @Linked private String title;

  JPanel getExtraButtonPanel() {
    return extraButtons;
  }

  Font getButtonFont() {
    return edit.getFont();
  }

  void setEditAvailable(boolean s) {
    edit.setEnabled(s);
  }

  void setRemoveAvailable(boolean s) {
    delete.setEnabled(s);
  }

  void setAddAvailable(boolean s) {
    add.setEnabled(s);
  }

  private void createUIComponents() {
    searchBoxView = searchBoxController.getView();
  }

  boolean commitDelete() {
    return JOptionPane.showConfirmDialog(
            getTopComponent(), "Soll dieses Objekt wirklich gelöscht werden?")
        == 0;
  }

  @Override
  public void initialize(ObjectViewController<T> controller) {
    add.setIcon(IconFontSwing.buildIcon(FontAwesome.PLUS, 20, new Color(71, 189, 23)));
    edit.setIcon(IconFontSwing.buildIcon(FontAwesome.PENCIL, 20, new Color(69, 189, 174)));
    delete.setIcon(IconFontSwing.buildIcon(FontAwesome.TRASH, 20, new Color(189, 101, 85)));
    add.addActionListener(e -> controller.add());
    edit.addActionListener(e -> controller.edit());
    delete.addActionListener(e -> controller.remove());
    back.addActionListener(e -> back());
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  @Override
  @StaticAccessPoint
  public IconCode getTabIcon() {
    return FontAwesome.PENCIL;
  }

  @Override
  public String getTitle() {
    return title;
  }

  public void messageSelectObjectFirst() {
    message("Bitte wähle zunächst ein Objekt aus.");
  }
}
