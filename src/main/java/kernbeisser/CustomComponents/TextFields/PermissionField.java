package kernbeisser.CustomComponents.TextFields;

import kernbeisser.Enums.Key;
import kernbeisser.Windows.LogIn.LogInModel;

import javax.swing.*;

public class PermissionField extends JTextField {
    private boolean read = true,write = true;
    public void setRequiredKeys(Key read, Key write){
        this.read = LogInModel.getLoggedIn().hasPermission(read);
        this.write = LogInModel.getLoggedIn().hasPermission(write);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled && write);
    }

    @Override
    public void setText(String t) {
        super.setText(read ? t : "************");
    }
}
