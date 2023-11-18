package kernbeisser.Useful;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.Main;

public class Icons {

  public static final Icon SHOP_ICON = getShopIcon();

  private static Icon getShopIcon() {
    try {
      URL imageUrl = ClassLoader.getSystemResource("Images/Icons/Icon.png");
      Image image = ImageIO.read(imageUrl);
      return new ImageIcon(image.getScaledInstance(24, 24, Image.SCALE_SMOOTH));
    } catch (IOException e) {
      Main.logger.error("Could not find Icon.png. Using database icon instead");
      return defaultIcon(FontAwesome.DATABASE, Color.BLACK);
    }
  }

  public static Icon defaultIcon(IconCode awesome, Color color) {
    return IconFontSwing.buildIcon(awesome, Tools.scaleWithLabelScalingFactor(14), color);
  }

  public static Icon barcodeIcon =
      IconFontSwing.buildIcon(
          FontAwesome.BARCODE, Tools.scaleWithLabelScalingFactor(16), Color.BLACK);

  static Icon trueIcon =
      IconFontSwing.buildIcon(
          FontAwesome.CHECK_SQUARE_O, Tools.scaleWithLabelScalingFactor(16), Color.BLACK);
  static Icon falseIcon =
      IconFontSwing.buildIcon(
          FontAwesome.SQUARE_O, Tools.scaleWithLabelScalingFactor(16), Color.BLACK);

  public static Icon booleanIcon(Boolean b) {
    return ((b != null && b) ? trueIcon : falseIcon);
  }
}
