package kernbeisser.CustomComponents;

import kernbeisser.Security.PermissionSet;
import kernbeisser.Windows.Controller;

import javax.swing.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ControllerButton  extends JButton {
    public <V extends Controller<?,?>>  ControllerButton(V controller, Consumer<V> action){
        setEnabled(PermissionSet.hasPermissions(controller.getRequiredKeys()));
        addActionListener(e -> action.accept(controller));
    }
}
