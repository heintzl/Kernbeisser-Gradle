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

    COMBO_BOX_SELECTION_BACKGROUND(){
        @Override
        public Color getColor() {
            return UIManager.getColor("ComboBox.selectionBackground");
        }
    },
    COMBO_BOX_SELECTION_FOREGROUND(){
        @Override
        public Color getColor() {
            return UIManager.getColor("ComboBox.selectionForeground");
        }
    };

    public Color getColor() {
        return null;
    }
}
