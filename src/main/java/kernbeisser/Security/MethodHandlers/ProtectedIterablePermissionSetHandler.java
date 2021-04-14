package kernbeisser.Security.MethodHandlers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import kernbeisser.Enums.PermissionKey;

public class ProtectedIterablePermissionSetHandler extends AbstractSecurityHandler {

  private final AbstractSecurityHandler parent;
  private final PermissionKey[] read;
  private final PermissionKey[] modify;

  @Override
  protected boolean canInvoke(PermissionKey[] keys) {
    boolean read = false, modify = false;

    ArrayList<PermissionKey> permissionKeys = new ArrayList<>(keys.length + this.read.length);
    for (PermissionKey key : keys) {
      permissionKeys.add(key);
      if (key.equals(PermissionKey.READ_ITERABLE_VALUE)) {
        read = true;
        continue;
      }
      if (key.equals(PermissionKey.MODIFY_ITERABLE_VALUE)) modify = true;
    }
    permissionKeys.removeIf(
        e ->
            e.equals(PermissionKey.READ_ITERABLE_VALUE)
                || e.equals(PermissionKey.MODIFY_ITERABLE_VALUE));
    if (read) {
      permissionKeys.addAll(Arrays.asList(this.read));
    }
    if (modify) {
      permissionKeys.addAll(Arrays.asList(this.modify));
    }
    return parent.canInvoke(permissionKeys.toArray(new PermissionKey[0]));
  }

  @Override
  protected RuntimeException accessDenied(PermissionKey[] keys, Method method) {
    return parent.accessDenied(keys, method);
  }

  public ProtectedIterablePermissionSetHandler(
      PermissionKey[] read, PermissionKey[] modify, AbstractSecurityHandler parent) {
    this.read = read;
    this.modify = modify;
    this.parent = parent;
  }

  @Override
  protected Object proxyChild(Object o) {
    return parent.proxyChild(o);
  }
}
