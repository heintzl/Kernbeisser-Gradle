package kernbeisser.Enums;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public enum Theme {
    DARK(new FlatDarkLaf()),LIGHT(new FlatLightLaf()),DARCULA(new FlatDarculaLaf()),INTELLIJ(new FlatIntelliJLaf())
    ;

    private final LookAndFeel lookAndFeel;
    Theme(LookAndFeel lookAndFeel){
     this.lookAndFeel = lookAndFeel;
    }

    public LookAndFeel getLookAndFeel() {
        return lookAndFeel;
    }
}
