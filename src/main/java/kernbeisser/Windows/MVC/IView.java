package kernbeisser.Windows.MVC;

import java.awt.*;
import java.util.Optional;
import javax.swing.*;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.Setting;
import kernbeisser.Security.StaticMethodTransformer.StaticAccessPoint;
import kernbeisser.Security.StaticMethodTransformer.StaticInterface;
import kernbeisser.Windows.ViewContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IView<
        C extends Controller<? extends IView<? extends C>, ? extends IModel<? extends C>>>
    extends StaticInterface {

  void initialize(C controller);

  @NotNull
  JComponent getContent();

  @NotNull
  default Dimension getSize() {
    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    Insets insets =
        Toolkit.getDefaultToolkit().getScreenInsets(this.getContent().getGraphicsConfiguration());
    dimension.setSize(
        dimension.getWidth() - insets.left - insets.right,
        dimension.height - insets.top - insets.bottom);
    if (DBConnection.isInitialized()) {
      dimension.setSize(
          Math.min(dimension.width, Setting.APP_DEFAULT_WIDTH.getIntValue()),
          Math.min(dimension.height, Setting.APP_DEFAULT_HEIGHT.getIntValue()));
    } else {
      dimension.setSize(
          Math.min(dimension.width, Integer.parseInt(Setting.APP_DEFAULT_WIDTH.getDefaultValue())),
          Math.min(
              dimension.height, Integer.parseInt(Setting.APP_DEFAULT_HEIGHT.getDefaultValue())));
    }
    return dimension;
  }

  default java.awt.Window getTopComponent() {
    return SwingUtilities.getWindowAncestor(getContent());
  }

  @StaticAccessPoint
  default IconCode getTabIcon() {
    return FontAwesome.WINDOW_MAXIMIZE;
  }

  default ViewContainer traceViewContainer() {
    return IView.traceViewContainer(getContent());
  }

  static ViewContainer traceViewContainer(Component init) {
    return ControllerReference.traceBack(init, e -> e.getController().getContainer() != null)
        .getController()
        .getContainer();
  }

  default void back() {
    ViewContainer viewContainer = traceViewContainer();
    if (viewContainer != null) {
      viewContainer.requestClose();
    }
  }

  @StaticAccessPoint
  default String getTitle() {
    return "";
  }

  @StaticAccessPoint
  default boolean isStackable() {
    return false;
  }

  default Component getFocusOnInitialize() {
    return getContent();
  }

  default void kill() {
    Optional.ofNullable(traceViewContainer()).ifPresent(ViewContainer::kill);
  }

  default void message(@NotNull String message) {
    message(message, "");
  }

  default void message(@NotNull String message, @Nullable String title) {
    message(message, title, JOptionPane.INFORMATION_MESSAGE);
  }

  default void message(@NotNull String message, @Nullable String title, int type) {
    JOptionPane.showMessageDialog(getContent(), message, title, type);
  }
}
