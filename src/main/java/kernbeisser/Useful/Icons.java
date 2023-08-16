package kernbeisser.Useful;

import java.awt.*;
import javax.swing.*;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

public class Icons {

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
