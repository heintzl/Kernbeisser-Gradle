package kernbeisser.Enums;

import javax.swing.*;
import java.awt.*;

public enum Colors {
    LABEL_FOREGROUND(){
        @Override
        public Color getColor() {
            return UIManager.getColor("Label.foreground");
        }
    },
    LABEL_BACKGROUND(){
        @Override
        public Color getColor() {
            return UIManager.getColor("Label.background");
        }
    },
    ;

    public Color getColor() {
        return null;
    }
}
