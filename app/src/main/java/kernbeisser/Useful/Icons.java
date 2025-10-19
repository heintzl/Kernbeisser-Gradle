package kernbeisser.Useful;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import javax.imageio.ImageIO;
import javax.swing.*;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Icons {

  public static final Icon SHOP_ICON = getShopIcon();

  private static Icon getShopIcon() {
    try {
      URL imageUrl = ClassLoader.getSystemResource("Images/Icons/Icon.png");
      Image image = ImageIO.read(imageUrl);
      return new ImageIcon(image.getScaledInstance(24, 24, Image.SCALE_SMOOTH));
    } catch (IOException e) {
      log.error("Could not find Icon.png. Using database icon instead");
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

  public static final Icon offerActiveIcon =
      IconFontSwing.buildIcon(
          FontAwesome.PERCENT, Tools.scaleWithLabelScalingFactor(20), Color.GREEN);

  public static final Icon offerInactiveIcon =
      IconFontSwing.buildIcon(
          FontAwesome.PERCENT, Tools.scaleWithLabelScalingFactor(20), Color.GRAY);

  public static Icon booleanIcon(Boolean b) {
    return ((b != null && b) ? trueIcon : falseIcon);
  }

  private static final Icon offerIcon =
      IconFontSwing.buildIcon(
          FontAwesome.PERCENT, Tools.scaleWithLabelScalingFactor(14), Color.BLUE);
  private static final Icon articleIcon =
      IconFontSwing.buildIcon(
          FontAwesome.CHECK, Tools.scaleWithLabelScalingFactor(16), new Color(0x199C00));
  private static final Icon noArticleIcon =
      IconFontSwing.buildIcon(
          FontAwesome.MINUS, Tools.scaleWithLabelScalingFactor(16), new Color(0xBD0000));

  public static final Icon clearInputIcon =
      IconFontSwing.buildIcon(FontAwesome.TIMES, 14, new Color(0x680C00));

  public static Icon catalogArticleIcon(Optional<Boolean> optionalOffer) {
    return optionalOffer.map(offer -> offer ? offerIcon : articleIcon).orElse(noArticleIcon);
  }
}
