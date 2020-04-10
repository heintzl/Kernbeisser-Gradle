package kernbeisser.CustomComponents.TextFields;

import kernbeisser.CustomComponents.RequiresPermission;

import javax.swing.*;

public class PermissionField extends JTextField implements RequiresPermission {
    private boolean read = true, write = true;
    private String original = "";

    @Override
    public void setReadable(boolean b) {
        write = b;
        setText(getText());
    }

    @Override
    public void setWriteable(boolean b) {
        read = b;
        setEnabled(isEnabled());
    }


    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled && write);
    }

    @Override
    public String getText() {
        return write ? super.getText() : original;
    }

    @Override
    public void setText(String t) {
        original = t;
        super.setText(read ? t : "************");
    }
}
