package kernbeisser.Windows.MVC;

import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.*;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import kernbeisser.CustomComponents.ViewMainPanel;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.Setting;
import org.jetbrains.annotations.NotNull;

public interface View<
    C extends Controller<? extends View<? extends C>, ? extends Model<? extends C>>> {

  void initialize(C controller);

  @NotNull
  JComponent getContent();

  @NotNull
  default Dimension getSize() {
    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    if (DBConnection.isInitialized()) {
      dimension.setSize(
          Math.min(dimension.getWidth(), Setting.APP_DEFAULT_WIDTH.getIntValue()),
          Math.min(dimension.getHeight(), Setting.APP_DEFAULT_HEIGHT.getIntValue()));
    } else {
      dimension.setSize(
          Math.min(
              dimension.getWidth(), Integer.parseInt(Setting.APP_DEFAULT_WIDTH.getDefaultValue())),
          Math.min(
              dimension.getHeight(),
              Integer.parseInt(Setting.APP_DEFAULT_HEIGHT.getDefaultValue())));
    }
    return dimension;
  }

  default java.awt.Window getTopComponent() {
    return SwingUtilities.getWindowAncestor(getContent());
  }

  default IconCode getTabIcon() {
    return FontAwesome.WINDOW_MAXIMIZE;
  }

  default kernbeisser.Windows.Window getWindow() {
    return (kernbeisser.Windows.Window) SwingUtilities.getWindowAncestor(getContent());
  }

  default void back() {
    kernbeisser.Windows.Window window = getWindow();
    if (window != null) {
      window.back();
    }
  }

  default String getTitle() {
    return "";
  }

  default boolean isStackable() {
    return false;
  }

  default ViewMainPanel getWrappedContent() {
    return new ViewMainPanel(getContent(), this);
  }

  default boolean processKeyboardInput(KeyEvent e) {
    if (e.getKeyCode() == Setting.SCANNER_PREFIX_KEY.getKeyEventValue()) {
      JOptionPane.showMessageDialog(
          getContent(), "In diesem Fenster ist keine Barcode-Eingabe möglich");
      return true;
    } else {
      return false;
    }
  }
}
