package kernbeisser.Windows;


import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public interface View <C extends Controller<? extends View<? extends C>,? extends Model<? extends C>>>{

    void initialize(C controller);

    @NotNull JComponent getContent();

    @NotNull default Dimension getSize(){
        return new Dimension(500,500);
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
}
