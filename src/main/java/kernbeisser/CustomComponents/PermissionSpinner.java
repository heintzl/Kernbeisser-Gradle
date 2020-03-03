package kernbeisser.CustomComponents;

import javax.swing.*;

public class PermissionSpinner extends JSpinner implements RequiresPermission{
    private boolean write = true,read = true;

    @Override
    public void setReadable(boolean b) {
        read = b;
        setValue(0);
    }

    @Override
    public void setWriteable(boolean b) {
        write = b;
        setEnabled(b);
    }

    @Override
    public void setValue(Object value) {
        super.setValue(read ? value : 0);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(write && enabled);
    }
}
