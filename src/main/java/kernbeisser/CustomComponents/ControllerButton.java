package kernbeisser.CustomComponents;

import jiconfont.swing.IconFontSwing;
import kernbeisser.Security.PermissionSet;
import kernbeisser.Windows.Controller;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ControllerButton  extends JButton {
    public <V extends Controller<?,?>>  ControllerButton(V controller, Consumer<V> action){
        setEnabled(PermissionSet.hasPermissions(controller.getRequiredKeys()));
        addActionListener(e -> action.accept(controller));
        setIcon(IconFontSwing.buildIcon(controller.getView().getTabIcon(),20,new Color(0xFF00CCFF)));
        setHorizontalAlignment(SwingConstants.LEFT);
    }
}
