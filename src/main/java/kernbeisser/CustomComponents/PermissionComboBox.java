package kernbeisser.CustomComponents;

import kernbeisser.Enums.Key;
import kernbeisser.Windows.LogIn.LogInModel;

import javax.swing.*;
import java.awt.*;

public class PermissionComboBox <T> extends JComboBox <T>{
    private boolean write = true;

    private void setRequiredReadKeys(Key... keys){
        setRenderer(LogInModel.getLoggedIn().hasPermission(keys) ? (list, value, index, isSelected, cellHasFocus) -> new JLabel("**********") : new DefaultListCellRenderer());
    }

    private void setRequiredWriteKeys(Key... keys){
        write = LogInModel.getLoggedIn().hasPermission(keys);
        setEnabled(write);
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(write && b);
    }
}
