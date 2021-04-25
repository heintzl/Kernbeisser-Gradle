package kernbeisser.CustomComponents;

import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.LogIn.LogInModel;

public interface RequiresPermission {

  void setReadable(boolean b);

  void setWriteable(boolean b);
}
