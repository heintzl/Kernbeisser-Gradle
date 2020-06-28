package kernbeisser.Security;

import kernbeisser.DBEntities.Permission;
import kernbeisser.Enums.Key;

import java.util.Arrays;
import java.util.Collection;

/**
 * loads given permission set into a bit field saved
 * as long array so we can easily check a permission
 * with bit field comparision against it.
 */

public final class PermissionSet {
    private static final long[] bits = new long[((Key.values().length / 64) + 1)];


    /**
     * loads all permission from the collection into the bit field
     * @param permissions the collection of all permission which the logged in user has.
     */
    public static void loadPermission(Collection<Permission> permissions){
        Arrays.fill(bits,0L);
        for (Permission permission : permissions) {
            for (Key key : permission.getKeySet()) {
                int o = key.ordinal();
                bits[o / 64] = (bits[o / 64] | (1<<(o%64)));
            }
        }
    }

    /**
     * checks if the PermissionSet contains a Key by the ordinal value of it
     * @param key the key which should get tested against the bit field
     * @return the boolean if the PermissionSet contains the permission
     */
    public static boolean hasPermission(Key key){
        long bit = (1<<(key.ordinal()%64));
        return (bits[key.ordinal() / 64] & bit) == bit;
    }


    /**
     * sets specific key in the PermissionSet to true
     * @param key the key which should added to the permission set
     */
    public static void addPermission(Key key){
        int o = key.ordinal();
        bits[o / 64] = (bits[o / 64] | (1<<(o%64)));
    }

    /**
     * removes the Key from the permission
     * @param key the key which should be removed
     */
    public static void removePermission(Key key){
        int o = key.ordinal();
        long bit =  (1<<(key.ordinal()%64));
        if((bits[key.ordinal() / 64] & bit) == bit)
        bits[o / 64] = (bits[o / 64] & (1<<(o%64)));
    }


    /**
     * {@link #hasPermission(Key)}
     * checks if the PermissionSet contains multiple keys
     * @param keys all keys in a var args form
     * @return true if the PermissionSet contains all keys
     */
    public static boolean hasPermissions(Key ... keys){
        for (Key key : keys) {
            if(!hasPermission(key))return false;
        }
        return true;
    }
}
