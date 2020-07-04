package kernbeisser.Security;

import kernbeisser.DBEntities.Permission;
import kernbeisser.Enums.Key;

import java.util.Arrays;
import java.util.Collection;

public final class PermissionSet {
    private static final long[] bits = new long[((Key.values().length / 64) + 1)];

    public static void loadPermission(Collection<Permission> permissions){
        Arrays.fill(bits,0L);
        for (Permission permission : permissions) {
            for (Key key : permission.getKeySet()) {
                int o = key.ordinal();
                bits[o / 64] = (bits[o / 64] | (1<<(o%64)));
            }
        }
    }

    public static boolean hasPermission(Key key){
        long bit = (1<<(key.ordinal()%64));
        return (bits[key.ordinal() / 64] & bit) == bit;
    }

    public static void addPermission(Key key){
        int o = key.ordinal();
        bits[o / 64] = (bits[o / 64] | (1<<(o%64)));
    }

    public static void removePermission(Key key){
        int o = key.ordinal();
        long bit =  (1<<(key.ordinal()%64));
        if((bits[key.ordinal() / 64] & bit) == bit)
        bits[o / 64] = (bits[o / 64] & (1<<(o%64)));
    }

    public static boolean hasPermissions(Key ... keys){
        for (Key key : keys) {
            if(!hasPermission(key))return false;
        }
        return true;
    }
}
