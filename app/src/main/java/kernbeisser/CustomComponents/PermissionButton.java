package kernbeisser.CustomComponents;

import javax.swing.*;

public class PermissionButton extends JButton implements RequiresPermission {
  private boolean read = true, write = true;

  @Override
  public void setReadable(boolean b) {
    read = b;
  }

  @Override
  public void setWriteable(boolean b) {
    write = b;
    setEnabled(b);
  }

  @Override
  public void setText(String text) {
    super.setText(read ? text : "********");
  }

  @Override
  public void setEnabled(boolean b) {
    super.setEnabled(write && b);
  }
}
