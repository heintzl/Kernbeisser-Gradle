package kernbeisser.CustomComponents;

import kernbeisser.Enums.Key;
import kernbeisser.Windows.LogIn.LogInModel;

import javax.swing.*;

public class PermissionCheckBox extends JCheckBox {
    private boolean read = true,write = true;

    public void setRequiredKeys(Key read, Key write){
        this.read = LogInModel.getLoggedIn().hasPermission(read);
        this.write = LogInModel.getLoggedIn().hasPermission(write);
        setEnabled(false);
    }

    public void setRequiredWriteKeys(Key ... keys){
        write = LogInModel.getLoggedIn().hasPermission(keys);
        setEnabled(write);
    }

    public void setRequiredReadKeys(Key ... keys){
        read = LogInModel.getLoggedIn().hasPermission(keys);
        if(!read)setEnabled(false);
    }

    @Override
    public void setSelected(boolean b) {
        super.setSelected(read && b);
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(write&&b);
    }
}
