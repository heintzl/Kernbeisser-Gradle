package kernbeisser.CustomComponents.TextFields;

import kernbeisser.Enums.Key;
import kernbeisser.Windows.LogIn.LogInModel;

import javax.swing.*;

public class PermissionField extends JTextField {
    private boolean read = true, write = true;
    private String original = "";

    public void setRequiredKeys(Key read, Key write) {
        this.read = LogInModel.getLoggedIn().hasPermission(read);
        this.write = LogInModel.getLoggedIn().hasPermission(write);
        setEnabled(false);
    }

    public void setRequiredWriteKeys(Key... keys) {
        write = LogInModel.getLoggedIn().hasPermission(keys);
        setEnabled(write);
    }

    public void setRequiredReadKeys(Key... keys) {
        read = LogInModel.getLoggedIn().hasPermission(keys);
        setText(getText());
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled && write);
    }

    @Override
    public void setText(String t) {
        original = t;
        super.setText(read ? t : "************");
    }

    @Override
    public String getText() {
        return write ? super.getText() : original;
    }
}
