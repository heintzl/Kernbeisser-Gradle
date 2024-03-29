package kernbeisser.CustomComponents;

import javax.swing.*;

public class PermissionCheckBox extends JCheckBox implements RequiresPermission {
  private boolean read = true, write = true;
  private boolean original;

  @Override
  public void setReadable(boolean b) {
    read = b;
    if (!b) {
      setSelected(false);
    }
  }

  @Override
  public void setWriteable(boolean b) {
    write = b;
    setEnabled(b);
  }

  @Override
  public void setSelected(boolean b) {
    original = b;
    super.setSelected(read && b);
  }

  @Override
  public boolean isSelected() {
    return read ? super.isSelected() : original;
  }

  @Override
  public void setEnabled(boolean b) {
    super.setEnabled(write && b);
  }
}
