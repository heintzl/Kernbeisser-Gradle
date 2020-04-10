package kernbeisser.Windows;


import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public interface View <C extends Controller<? extends View<? extends C>,? extends Model<? extends C>>>{
    void initialize(C controller);
    @NotNull JComponent getContent();
    @NotNull default Dimension getSize(){
        return new Dimension(500,500);
    };

    default Component getTopComponent(){
        return SwingUtilities.getWindowAncestor(getContent());
    }

    default Window getWindow(){
        return (Window) SwingUtilities.getWindowAncestor(getContent());
    }

    default void back(){
        Window window = getWindow();
        if(window!=null)window.back();
    }
}
