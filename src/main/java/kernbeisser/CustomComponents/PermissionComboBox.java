package kernbeisser.CustomComponents;

import javax.swing.*;

public class PermissionComboBox<T> extends JComboBox<T> implements RequiresPermission {
    private boolean write = true;

    @Override
    public void setReadable(boolean b) {
        setRenderer(b
                    ? (list,value,index,isSelected,cellHasFocus) -> new JLabel("**********")
                    : new DefaultListCellRenderer());
    }

    @Override
    public void setWriteable(boolean b) {
        write = b;
        setEnabled(write);
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(write && b);
    }
}
