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

  public static Icon barcodeIcon() {
    return IconFontSwing.buildIcon(
        FontAwesome.BARCODE, Tools.scaleWithLabelScalingFactor(16), Color.BLACK);
  }
}
