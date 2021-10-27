package kernbeisser.Useful;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

import javax.swing.*;
import java.awt.*;

public class Icons {


    public static Icon defaultIcon(FontAwesome awesome, Color color){
        return IconFontSwing.buildIcon(awesome,Tools.scaleWithLabelScalingFactor(14),color);
    }
}
