package kernbeisser.Useful;

import java.awt.*;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;

public class Constants {

  public static final int SYSTEM_DBLCLK_INTERVAL = getSystemDblClkInterval();
  public static final User SHOP_USER = User.getShopUser();
  public static final int SHOP_USER_ID = SHOP_USER.getId();
  public static final UserGroup SHOP_USERGROUP = SHOP_USER.getUserGroup();
  public static final Supplier KK_SUPPLIER = Supplier.getKKSupplier();
  public static final Supplier DEPOSIT_SUPPLIER = Supplier.getDepositSupplier();
  public static final Supplier SOLIDARITY_SUPPLIER = Supplier.getSolidaritySupplier();
  public static final Supplier BAKERY_SUPPLIER = Supplier.getBakerySupplier();
  public static final Supplier PRODUCE_SUPPLIER = Supplier.getProduceSupplier();

  private static int getSystemDblClkInterval() {
    Object interval = Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");
    if (interval instanceof Integer) {
      return ((int) interval) * (int) 1e+6;
    } else {
      return (int) 5e+8;
    }
  }
}
