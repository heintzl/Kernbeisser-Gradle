package kernbeisser.Useful;

import java.awt.*;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;

public class Constants {

  public static final int SYSTEM_DBLCLK_INTERVAL = getSystemDblClkInterval();
  public static final User SHOP_USER = User.getKernbeisserUser();
  public static final UserGroup SHOP_USERGROUP = SHOP_USER.getUserGroup();
  public static final Supplier KK_SUPPLIER = Supplier.getKKSupplier();

  private static int getSystemDblClkInterval() {
    Object interval = Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");
    if (interval instanceof Integer) {
      return ((int) interval) * (int) 1e+6;
    } else {
      return (int) 5e+8;
    }
  }
}
