package kernbeisser.Useful;

import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;

import java.awt.*;

public class Constants {

    public final static int SYSTEM_DBLCLK_INTERVAL = getSystemDblClkInterval();
    public final static User SHOP_USER = User.getKernbeisserUser();
    public final static UserGroup SHOP_USERGROUP = SHOP_USER.getUserGroup();
    public final static Supplier KK_SUPPLIER = Supplier.getKKSupplier();

    private static int getSystemDblClkInterval() {
        Object interval = Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");
        if (interval instanceof Integer) {
            return ((int) interval) * (int) 1e+6;
        } else {
            return (int) 5e+8;
        }
    }

}
