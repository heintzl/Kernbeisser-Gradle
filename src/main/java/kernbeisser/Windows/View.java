package kernbeisser.Windows;


import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import kernbeisser.CustomComponents.ViewMainPanel;
import kernbeisser.Enums.Setting;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public interface View <C extends Controller<? extends View<? extends C>,? extends Model<? extends C>>>{

    void initialize(C controller);

    @NotNull JComponent getContent();

    @NotNull default Dimension getSize(){
        return new Dimension(1600,1000);
    };

    default java.awt.Window getTopComponent(){
        return SwingUtilities.getWindowAncestor(getContent());
    }

    default IconCode getTabIcon(){
        return FontAwesome.WINDOW_MAXIMIZE;
    }

    default Window getWindow(){
        return (Window) SwingUtilities.getWindowAncestor(getContent());
    }

    default void back(){
        Window window = getWindow();
        if(window != null)
            window.back();
    }

    default String getTitle(){
        return "";
    }

    default boolean isStackable(){
        return false;
    }

    default ViewMainPanel getWrappedContent() {
        return new ViewMainPanel(getContent(), this);
    }

    default boolean processKeyboardInput(KeyEvent e) {
        if (e.getKeyCode() == Setting.SCANNER_PREFIX_KEY.getIntValue()) {
            JOptionPane.showMessageDialog(e.getComponent(), "In diesem Fenster ist keine Barcode-Eingabe möglich");
            return true;
        } else {
            return e.getKeyCode() == Setting.SCANNER_SUFFIX_KEY.getIntValue();
        }
    }
}
