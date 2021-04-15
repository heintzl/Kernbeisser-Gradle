package kernbeisser.Security.MethodHandlers;

import java.lang.reflect.Method;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Security.PermissionSet;
import lombok.Getter;

public class PermissionSetSecurityHandler extends AbstractSecurityHandler {

  public static PermissionSetSecurityHandler ON_LOGGED_IN =
      new PermissionSetSecurityHandler(PermissionSet.MASTER);

  public static PermissionSetSecurityHandler IN_RELATION_TO_OWN_USER =
      new PermissionSetSecurityHandler(PermissionSet.IN_RELATION_TO_USER);

  @Getter private final PermissionSet permissionSet;

  public PermissionSetSecurityHandler(PermissionSet keys) {
    this.permissionSet = keys;
  }

  public PermissionSetSecurityHandler(PermissionKey... keys) {
    this(new PermissionSet());
    for (PermissionKey key : keys) {
      permissionSet.addPermission(key);
    }
  }

  public static PermissionSet permissionKeys = new PermissionSet();

  @Override
  protected boolean canInvoke(PermissionKey[] keys) {
    return permissionSet.hasPermissions(keys);
  }

  @Override
  protected RuntimeException accessDenied(PermissionKey[] keys, Method method) {
    return new PermissionKeyRequiredException(permissionSet, keys, method.getName());
  }
}
